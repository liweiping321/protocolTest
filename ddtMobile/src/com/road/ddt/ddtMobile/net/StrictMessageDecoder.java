package com.road.ddt.ddtMobile.net;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.mina.core.buffer.IoBuffer;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.CumulativeProtocolDecoder;
import org.apache.mina.filter.codec.ProtocolDecoderOutput;

import com.game.utils.DateUtil;
 

/**
 * @author lucas.lu
 *         客户端解密处理
 */
public class StrictMessageDecoder extends CumulativeProtocolDecoder
{
	private static final Logger LOGGER = LogManager.getLogger(StrictMessageDecoder.class);
    @Override
    protected boolean doDecode(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
    {
        return decode2(session, in, out);
    }

    // 获取密钥上下文
    private int[] getContext(IoSession session)
    {
        int[] keys = (int[]) session.getAttribute(CommonConst.DECRYPTION_KEY);
        if (keys == null)
        {
            keys = new int[]
            { 0xae, 0xbf, 0x56, 0x78, 0xab, 0xcd, 0xef, 0xf1 };

            session.setAttribute(CommonConst.DECRYPTION_KEY, keys);
        }

        return keys;
    }

    protected boolean decode2(IoSession session, IoBuffer in, ProtocolDecoderOutput out) throws Exception
    {
        Long m = (Long) session.getAttribute("expiredMask", 1L);
        if (m == 1)
        {
            session.setAttribute("expiredMask", 0L);
            session.setAttribute("expired", DateUtil.getSysCurTimeMillis());
        }
        else
        {
            Long t = (Long) session.getAttribute("expired", DateUtil.getSysCurTimeMillis());
            if (DateUtil.getSysCurTimeMillis() - t > 3000L)
            {
                Object userName = session.getAttribute(CommonConst.USER_NAME);
                Object userId = session.getAttribute(CommonConst.USER_ID);
                LOGGER.debug(String.format("Pack decode 3 seconds timeout. RemoteAddress:%s UserName:%s UserID:%s",
                        session.getRemoteAddress(), userName, userId));
                in.flip();
                session.closeNow();
                return false;
            }
        }

        if (in.remaining() < 4)
        {
            return false;
        }

        final int cur = in.position();
        int header = 0;
        int packetLength = 0;
        int[] decryptKey = getContext(session);

        int cipherByte1 = 0, cipherByte2 = 0, cipherByte3, cipherByte4;

        // 此处4字节头部的解码使用直接解码形式，规避频繁的对象创建
        int plainByte1, plainByte2, plainByte3, plainByte4;
        int key;

        // 解密两字节header
        cipherByte1 = in.get() & 0xff;
        key = decryptKey[0];
        plainByte1 = (cipherByte1 ^ key) & 0xff;

        cipherByte2 = in.get() & 0xff;
        key = ((decryptKey[1] + cipherByte1) ^ 1) & 0xff;
        plainByte2 = ((cipherByte2 - cipherByte1) ^ key) & 0xff;

        header = ((plainByte1 << 8) + plainByte2);

        // 解密两字节length
        cipherByte3 = in.get() & 0xff;
        key = ((decryptKey[2] + cipherByte2) ^ 2) & 0xff;
        plainByte3 = ((cipherByte3 - cipherByte2) ^ key) & 0xff;

        cipherByte4 = in.get() & 0xff;
        key = ((decryptKey[3] + cipherByte3) ^ 3) & 0xff;
        plainByte4 = ((cipherByte4 - cipherByte3) ^ key) & 0xff;
        packetLength = (plainByte3 << 8) + plainByte4;

        // 预解密长度信息成功，回溯位置
        in.position(in.position() - 4);

        if (header != CommonMessage.HEADER)
        {
            in.position(in.position() + 2);

            Object userName = session.getAttribute(CommonConst.USER_NAME);
            Object userId = session.getAttribute(CommonConst.USER_ID);
            LOGGER.debug(String.format("packet header is error, header:%d RemoteAddress:%s UserName:%s UserID:%d in.remaining()=%d",
                    header, session.getRemoteAddress(), userName, userId, in.remaining()));

            /*
             * LOGGER.debug(String.format("KEY getContext:(%02X %02X %02X %02X %02X %02X %02X %02X)...", decryptKey[0],
             * decryptKey[1], decryptKey[2], decryptKey[3], decryptKey[4], decryptKey[5], decryptKey[6],
             * decryptKey[7]));
             * LOGGER.debug(String.format("HEAD1...(%02X %02X %02X %02X)...", cipherByte1, cipherByte2, cipherByte3,
             * cipherByte4));
             * LOGGER.debug(
             * String.format("HEAD2...(%02X %02X %02X %02X)...", plainByte1, plainByte2, plainByte3, plainByte4));
             */

            in.flip();
            session.closeNow();
            return false;
        }

        if (packetLength < CommonMessage.HDR_SIZE)
        {
            Object userName = session.getAttribute(CommonConst.USER_NAME);
            Object userId = session.getAttribute(CommonConst.USER_ID);
            LOGGER.debug(String.format(
                    "Error packet length, Disconnect the client: RemoteAddress:%s UserName:%s UserID:%d in.remaining()=%d",
                    session.getRemoteAddress(), userName, userId, in.remaining()));
            /*
             * LOGGER.debug(String.format("KEY getContext:(%02X %02X %02X %02X %02X %02X %02X %02X)...", decryptKey[0],
             * decryptKey[1], decryptKey[2], decryptKey[3], decryptKey[4], decryptKey[5], decryptKey[6],
             * decryptKey[7]));
             * LOGGER.debug(String.format("HEAD1...(%02X %02X %02X %02X)...", cipherByte1, cipherByte2, cipherByte3,
             * cipherByte4));
             * LOGGER.debug(
             * String.format("HEAD2...(%02X %02X %02X %02X)...", plainByte1, plainByte2, plainByte3, plainByte4));
             */

            in.flip();
            session.closeNow();
            return false;
        }

        if (in.remaining() < packetLength)
        {
            return false;
        }

        decrypt(in, decryptKey, cur, packetLength);
        CommonMessage packet = CommonMessage.build(in, cur);

        if (packet != null)
            out.write(packet);
        else
            LOGGER.error("packet is null");

        session.setAttribute("expiredMask", 1L);

        return true;
    }

    private void decrypt(IoBuffer in, int[] decryptKey, int cur, int length)
    {
        byte lastCipherByte;
        byte plainText;
        byte key;

        // 解密首字节
        key = (byte) decryptKey[0];
        lastCipherByte = in.get(cur);
        plainText = (byte) (in.get(cur) ^ key);
        in.put(0, plainText);

        for (int index = 1; index < length; index++)
        {
            int buffIdx = index + cur;
            // 解密当前字节
            key = (byte) ((decryptKey[index & 0x7] + lastCipherByte) ^ index);
            plainText = (byte) (((in.get(buffIdx) & 0xff) - lastCipherByte) ^ key);

            // 更新变量值
            lastCipherByte = in.get(buffIdx);
            in.put(buffIdx, plainText);
            decryptKey[index & 0x7] = (byte) (key & 0xff);
        }
    }
}
