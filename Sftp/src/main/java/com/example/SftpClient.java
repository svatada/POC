package com.example;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import org.apache.log4j.Logger;

import java.security.Security;
import java.util.Arrays;
import java.util.Scanner;

public class SftpClient {
    final static Logger LOGGER = Logger.getLogger(SftpClient.class);
    private static final int SESSION_TIMEOUT = 10000;
    private static final int CHANNEL_TIMEOUT = 5000;

    public static void main(String[] args) {
        System.out.println("Printing registered providers:" );
        Arrays.stream(Security.getProviders()).forEach(provider -> LOGGER.info(provider));
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nENTER SFTP HOST:");
        String REMOTE_HOST = scanner.next();
        System.out.println("ENTER SFTP PORT:");
        int REMOTE_PORT = scanner.nextInt();
        System.out.println("ENTER SFTP USERNAME:");
        String USERNAME = scanner.next();
        System.out.println("ENTER SFTP PASSWORD:");
        String PASSWORD = scanner.next();
        System.out.println("ENTER LOCAL FILE PATH TO COPY:");
        String localFile = scanner.next();
        System.out.println("ENTER SFTP REMOTE FILE LOCATION:");
        String remoteFile = scanner.next();
        Session jschSession = null;

        try {
            JSch jsch = new JSch();
            JSch.setLogger(new JschLogger(LOGGER));
            jsch.setKnownHosts("~/.ssh/known_hosts");
            try {
                System.out.println(jsch.getConfigRepository().toString());
            } catch (Exception e) {

            }
            LOGGER.info("Creating jsch Session...");
            jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);

            // authenticate using private key
            // jsch.addIdentity("/home/gaian/.ssh/id_rsa");

            // authenticate using password
            jschSession.setPassword(PASSWORD);
            // 10 seconds session timeout
            jschSession.connect(SESSION_TIMEOUT);
            LOGGER.info("Opening sftp channel...");
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            LOGGER.info("Connecting to sftp...");
            sftp.connect(CHANNEL_TIMEOUT);
            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // transfer file from local to remote server
            LOGGER.info("Writing file to sftp folder...");
            channelSftp.put(localFile, remoteFile);
            // download file from remote server to local
            // channelSftp.get(remoteFile, localFile);
            channelSftp.exit();
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
        } finally {
            if (jschSession != null) {
                jschSession.disconnect();
            }
        }
        System.out.println("Done");
    }
}