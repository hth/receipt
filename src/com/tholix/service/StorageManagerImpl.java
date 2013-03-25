/**
 * 
 */
package com.tholix.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.bson.types.ObjectId;

import com.mongodb.DB;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.tholix.domain.UploadReceiptImage;

/**
 * @author hitender
 * @when Jan 3, 2013 3:09:08 AM
 * 
 */
public class StorageManagerImpl implements StorageManager {
	private static final long serialVersionUID = -5264258042433041673L;
	private final Log log = LogFactory.getLog(getClass());

	private final GridFS gridFs;

	public StorageManagerImpl(DB gridfsDb) {
		gridFs = new GridFS(gridfsDb);
	}

	@Override
	public List<UploadReceiptImage> getAllObjects() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void saveObject(UploadReceiptImage object) throws Exception {
		save(object.getFileData().getInputStream(), object.getFileData().getContentType(), object.getFileName());
	}

	@Override
	public String save(UploadReceiptImage object) throws IOException {
		return save(object.getFileData().getInputStream(), object.getFileData().getContentType(), object.getFileName());
	}

	@Override
	public UploadReceiptImage getObject(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public WriteResult updateObject(String id, String name) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void deleteObject(String id) {
		log.debug("deleted GridFs object - " + id);
		gridFs.remove(new ObjectId(id));
	}

	@Override
	public void createCollection() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dropCollection() {
		// TODO Auto-generated method stub

	}

	@Override
	public String save(InputStream inputStream, String contentType, String filename) {
		boolean closeStreamOnPersist = true;
		GridFSInputFile receiptBlob = gridFs.createFile(inputStream, filename, closeStreamOnPersist);
		receiptBlob.setContentType(contentType);
		receiptBlob.save();
		return receiptBlob.getId().toString();
	}

	@Override
	public GridFSDBFile get(String id) {
		try {
			return gridFs.findOne(new ObjectId(id));
		} catch(IllegalArgumentException iae) {
			log.error("Submitted image id " + id + ", error mesaage - " + iae.getLocalizedMessage());
			return null;
		}
	}

	@Override
	public GridFSDBFile getByFilename(String filename) {
		return gridFs.findOne(filename);
	}
}
