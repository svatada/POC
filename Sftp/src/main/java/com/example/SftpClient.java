package com.example;

import com.jcraft.jsch.*;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Security;
import java.util.Arrays;
import java.util.Properties;
import java.util.Scanner;

public class SftpClient {
    private static final int SESSION_TIMEOUT = 10000;
    private static final int CHANNEL_TIMEOUT = 5000;

    public static void main(String[] args) {
        Security.addProvider(new BouncyCastleProvider());
        System.out.println("Printing registered providers:");
        Arrays.stream(Security.getProviders()).forEach(provider -> System.out.println(provider));
        Scanner scanner = new Scanner(System.in);
        System.out.println("\nENTER SFTP HOST:");
        String REMOTE_HOST = scanner.next();
        System.out.println("ENTER SFTP PORT:");
        int REMOTE_PORT = scanner.nextInt();
        System.out.println("ENTER SFTP USERNAME:");
        String USERNAME = scanner.next();
        System.out.println("ENTER SFTP PASSWORD:");
        String PASSWORD = scanner.next();
        System.out.println("ENTER LOCAL FILE PATH:");
        String localFile = scanner.next();
        System.out.println("ENTER REMOTE FILE LOCATION:");
        String remoteFile = scanner.next();
        Session jschSession = null;

        try {
            JSch jsch = new JSch();
            jsch.setKnownHosts("~/.ssh/known_hosts");
            // none of the algorithms supported by jsch
            // programatically enable sha256:
            Properties config = new Properties();
            config.put("kex", "diffie-hellman-group1-sha1,diffie-hellman-group14-sha1,diffie-hellman-group-exchange-sha1,diffie-hellman-group-exchange-sha256");

            System.out.println("Creating jsch Session...");
            jschSession = jsch.getSession(USERNAME, REMOTE_HOST, REMOTE_PORT);

            // authenticate using private key
            // jsch.addIdentity("/home/gaian/.ssh/id_rsa");

            // authenticate using password
            jschSession.setPassword(PASSWORD);
            jschSession.setConfig(config);
            // 10 seconds session timeout
            jschSession.connect(SESSION_TIMEOUT);
            System.out.println("Opening sftp channel...");
            Channel sftp = jschSession.openChannel("sftp");
            // 5 seconds timeout
            System.out.println("Connecting to sftp...");
            sftp.connect(CHANNEL_TIMEOUT);
            ChannelSftp channelSftp = (ChannelSftp) sftp;
            // transfer file from local to remote server
            System.out.println("Writing file to sftp folder...");
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