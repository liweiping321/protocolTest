package com.game;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import com.game.bean.ModuleBean;
import com.game.bean.ProtoFile;
import com.game.bean.ProtoMessage;

import freemarker.template.Configuration;
import freemarker.template.Template;

public class LoadProtos
{

    public static void main(String[] args) throws Exception
    {

        List<ProtoFile> requestFileList = parseProtoFiles("E://Mobilephone_DDT//Server//PBProtocol//proto//c2s");

        // createCheckFile(requestFileList, "requect_Checklist.txt");

        List<ProtoFile> responseFileList = parseProtoFiles("E://Mobilephone_DDT//Server//PBProtocol//proto//s2c");

        // createCheckFile(responseFileList, "response_Checklist.txt");

        responseFileList.addAll(requestFileList);
        craeteProtoConfig(responseFileList);
    }

    private static void craeteProtoConfig(List<ProtoFile> protoFiles) throws Exception
    {
        Map<String, List<ProtoFile>> protoFileGroup = new HashMap<>();

        for (ProtoFile protoFile : protoFiles)
        {
            List<ProtoFile> fileList = protoFileGroup.get(protoFile.getDirName());
            if (fileList == null)
            {
                fileList = new ArrayList<>();
                protoFileGroup.put(protoFile.getDirName(), fileList);
            }
            fileList.add(protoFile);
        }

        List<ModuleBean> moduleBeans = new ArrayList<>();
        for (Map.Entry<String, List<ProtoFile>> entry : protoFileGroup.entrySet())
        {
            ModuleBean moduleBean = new ModuleBean();
            moduleBean.setName(entry.getKey());
            for (ProtoFile protoFile : entry.getValue())
            {
                for (ProtoMessage protoMsg : protoFile.getProtoMsg())
                {
                    String className = protoFile.getJavaPackage() + "." + protoFile.getClassName() + "$"
                            + protoMsg.getMsgName();
                    protoMsg.setClassName(className);
                    moduleBean.getMsgs().add(protoMsg);
                }

            }
            moduleBeans.add(moduleBean);
        }
        Configuration cfg = new Configuration();
        cfg.setDirectoryForTemplateLoading(new File("template"));
        Map<String, Object> rootMap = new HashMap<>();
        rootMap.put("modules", moduleBeans);
        Template template = cfg.getTemplate("ProtoConfig.txt");
        template.process(rootMap, new FileWriter("ProtoConfig.xml"));
    }

    private static void createCheckFile(List<ProtoFile> protoFileList,
            String fileName) throws Exception
    {
        TreeMap<Integer, String> map = new TreeMap<>();

        for (ProtoFile protoFile : protoFileList)
        {
            for (ProtoMessage protoMsg : protoFile.getProtoMsg())
            {
                String value = protoMsg.getCode() + " = "
                        + protoFile.getJavaPackage() + "."
                        + protoFile.getClassName() + "$"
                        + protoMsg.getMsgName() + " " + protoMsg.getDesc();
                map.put(protoMsg.getCode(), value);
            }
        }

        FileUtils.writeLines(new File(fileName), map.values());
    }

    private static List<ProtoFile> parseProtoFiles(String dirPath)
            throws IOException
    {
        File file = new File(dirPath);
        List<ProtoFile> protoFileList = new ArrayList<>();
        File[] dirs = file.listFiles();
        for (File dir : dirs)
        {
            File[] protoFiles = dir.listFiles();
            for (File protFile : protoFiles)
            {
                ProtoFile protoFile;
                try
                {
                    protoFile = parsePotoFile(protFile);
                    protoFile.setDirName(dir.getName());
                    if (protoFile.getClassName() != null)
                    {
                        protoFileList.add(protoFile);
                    }

                }
                catch (Exception e)
                {
                    e.printStackTrace();
                    System.out.println(protFile.getName());
                }

            }
        }
        return protoFileList;
    }

    private static ProtoFile parsePotoFile(File protFile) throws Exception
    {
        ProtoFile protoFile = new ProtoFile();
        List<String> lines = FileUtils.readLines(protFile);

        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);
            if (line.contains("java_package"))
            {
                protoFile.setJavaPackage(StringUtils.substringBetween(line,
                        "\"", "\""));
                continue;
            }

            if (line.contains("java_outer_classname"))
            {
                protoFile.setClassName(StringUtils.substringBetween(line, "\"",
                        "\""));
                continue;
            }

            if (line.contains("message")
                    && lines.get(i - 1).contains("//proto"))
            {
                try
                {

                    String[] arr = lines.get(i - 2).split("=");
                    if (arr.length == 2)
                    {
                        String[] codes = arr[1].trim().split("\\s+");
                        for (String code : codes)
                        {
                            ProtoMessage protoMessage = new ProtoMessage();
                            protoMessage.setDesc(lines.get(i - 3).trim());
                            protoMessage.setCode(code);
                            protoMessage.setMsgName(line
                                .replaceAll("message", "")
                                .replaceAll("\\{", "").trim());
                            if (protFile.getAbsolutePath().contains("c2s"))
                            {
                                protoMessage.setDirectType("UP");
                            }
                            else if (protFile.getAbsolutePath().contains(
                                    "s2c"))
                            {
                                protoMessage.setDirectType("DOWN");
                            }

                            protoFile.getProtoMsg().add(protoMessage);
                        }

                    }
                    else
                    {
                        continue;
                    }
                }
                catch (Exception e)
                {
                    System.out.println(line);
                    throw e;
                }
            }
        }
        return protoFile;
    }
}
