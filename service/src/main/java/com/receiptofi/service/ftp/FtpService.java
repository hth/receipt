package com.receiptofi.service.ftp;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.commons.io.FileUtils;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.FileSystemOptions;
import org.apache.commons.vfs2.Selectors;
import org.apache.commons.vfs2.impl.DefaultFileSystemManager;
import org.apache.commons.vfs2.impl.StandardFileSystemManager;
import org.apache.commons.vfs2.provider.sftp.SftpFileSystemConfigBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.InputStream;

/**
 * Uploads and downloads files via FTP to loader server where expense reports are saved locally.
 * User: hitender
 * Date: 9/21/16 5:27 PM
 */
@Service
public class FtpService {
    private static final Logger LOG = LoggerFactory.getLogger(FtpService.class);

    @Value ("${fileserver.ftp.host}")
    private String host;

    @Value ("${expensofiReportLocation}")
    private String expensofiReportLocation;

    @Value ("${fileserver.ftp.username}")
    private String ftpUser;

    @Value ("${fileserver.ftp.password}")
    private String ftpPassword;

    private FileSystemOptions fileSystemOptions;

    private FtpService() {
        fileSystemOptions = createDefaultOptions();
    }

    public boolean exist() {
        DefaultFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();
            FileObject remoteFile = manager.resolveFile(createConnectionString(expensofiReportLocation), fileSystemOptions);
            return remoteFile.isFolder() && remoteFile.isWriteable() && remoteFile.isReadable();
        } catch (FileSystemException | URIException e) {
            /* Check if access set correctly for the user and remote location exists. */
            LOG.error("Could not find remote file={} reason={}", expensofiReportLocation, e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    public InputStream getFile(String filename) {
        DefaultFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();
            FileObject remoteFile = manager.resolveFile(createConnectionString(expensofiReportLocation + File.separator + filename), fileSystemOptions);
            if (remoteFile.exists() && remoteFile.isFile()) {
                return remoteFile.getContent().getInputStream();
            }
            LOG.error("Could not find file={}", filename);
            return null;
        } catch (FileSystemException | URIException e) {
            LOG.error("Failed to get file={} reason={}", filename, e.getLocalizedMessage(), e);
            return null;
        }
    }

    public void upload(String filename) {
        File file = new File(FileUtils.getTempDirectoryPath() + File.separator + filename);
        if (!file.exists()) {
            throw new RuntimeException("Error. Local file not found");
        }

        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            /* Create local file object. */
            FileObject localFile = manager.resolveFile(file.getAbsolutePath());

            /* Create remote file object. */
            FileObject remoteFile = manager.resolveFile(createConnectionString(expensofiReportLocation + File.separator + filename), fileSystemOptions);

            /* Copy local file to sftp server. */
            remoteFile.copyFrom(localFile, Selectors.SELECT_SELF);

            LOG.info("File upload success");
        } catch (FileSystemException | URIException e) {
            LOG.error("upload {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    public boolean delete(String filename) {
        StandardFileSystemManager manager = new StandardFileSystemManager();

        try {
            manager.init();

            /* Create remote object. */
            FileObject remoteFile = manager.resolveFile(createConnectionString(expensofiReportLocation + File.separator + filename), fileSystemOptions);

            if (remoteFile.exists()) {
                remoteFile.delete();
                LOG.info("Existing excel file={} deleted", filename);
                return true;
            }

            return false;
        } catch (FileSystemException | URIException e) {
            LOG.error("upload {}", e.getLocalizedMessage(), e);
            throw new RuntimeException(e);
        } finally {
            manager.close();
        }
    }

    private String createConnectionString(String filePath) throws URIException {
        return new URI(
                "sftp",
                ftpUser + ":" + ftpPassword,
                host,
                -1,
                filePath,
                null,
                null
        ).toString();
    }

    private FileSystemOptions createDefaultOptions() {
        try {
            /* Create SFTP options. */
            FileSystemOptions opts = new FileSystemOptions();

            /* SSH Key checking. */
            SftpFileSystemConfigBuilder.getInstance().setStrictHostKeyChecking(opts, "no");

            /*
             * Using the following line will cause VFS to choose File System's Root
             * as VFS's root. If I wanted to use User's home as VFS's root then set
             * 2nd method parameter to "true".
             */
            SftpFileSystemConfigBuilder.getInstance().setUserDirIsRoot(opts, false);

            /* Timeout is count by Milliseconds. */
            SftpFileSystemConfigBuilder.getInstance().setTimeout(opts, 10000);

            LOG.info("Created default options for VFS");
            return opts;
        } catch (FileSystemException e) {
            LOG.error("Error creating VFS filesystem {}", e.getLocalizedMessage(), e);
            return null;
        }
    }
}
