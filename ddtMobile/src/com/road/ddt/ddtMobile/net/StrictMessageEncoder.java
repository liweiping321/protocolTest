package com.road.ddt.ddtMobile.net;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolEncoderAdapter;
import org.apache.mina.filter.codec.ProtocolEncoderOutput;

public class StrictMessageEncoder extends ProtocolEncoderAdapter
{
	private static final Logger LOGGER = LogManager.getLogger(StrictMessageDecoder.class);
    public StrictMessageEncoder()
    {

    }

    @Override
    public void encode(IoSession session, Object message, ProtocolEncoderOutput out) throws Exception
    {
        synchronized (session)
        {
            try
            {
                CommonMessage msg = (CommonMessage) message;
                int len = msg.getLen();
                if (len < 0 || len > Short.MAX_VALUE)
                {
                	LOGGER.error(String.format("CommonMessage's length is too long. len is %d code is %d userID is %d", len, msg.getCode(), msg.getUserID()));
                    return;
                }

                IoBuffer buffer = IoBuffer.allocate(len);
                msg.writeTo(buffer);
                msg.writeCalcSum(buffer);

                encrty(session, buffer, len);

                buffer.flip();
                out.write(buffer);
                out.flush();
            }
            catch (Exception ex)
            {
            	LOGGER.error("catch error for encoding packet:", ex);
            }
        }
    }

    private int[] getContext(IoSession session)
    {
        int[] keys = (int[]) session.getAttribute(CommonConst.ENCRYPTION_KEY);
        if (keys == null)
        {
            keys = new int[]
            { 0xae, 0xbf, 0x56, 0x78, 0xab, 0xcd, 0xef, 0xf1 };

            session.setAttribute(CommonConst.ENCRYPTION_KEY, keys);
        }
        return keys;
    }

    private void encrty(IoSession session, IoBuffer buffer, int len)
    {
        int lastCipherByte = 0;
        int[] encryptKey = getContext(session);

        lastCipherByte = (byte) ((buffer.get(0) ^ encryptKey[0]) & 0xff);
        buffer.put(0, (byte) lastCipherByte);

        // 循环加密
        int keyIndex = 0;
        for (int i = 1; i < len; i++)
        {
            keyIndex = i & 0x7;
            encryptKey[keyIndex] = ((encryptKey[keyIndex] + lastCipherByte) ^ i) & 0xff;
            lastCipherByte = (((buffer.get(i) ^ encryptKey[keyIndex]) & 0xff) + lastCipherByte) & 0xff;
            buffer.put(i, (byte) lastCipherByte);
        }
    }
}
