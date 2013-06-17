/**
 *
 */
package com.tholix.repository;

import org.bson.types.ObjectId;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;

import com.tholix.domain.UploadReceiptImage;

/**
 * @author hitender
 * @since Jan 3, 2013 3:09:08 AM
 *
 */
@Transactional(readOnly = true)
public class StorageManagerImpl implements StorageManager {
	private static final long serialVersionUID = -5264258042433041673L;
	private static final Logger log = Logger.getLogger(StorageManagerImpl.class);

	private final GridFS gridFs;

	public StorageManagerImpl(DB gridfsDb) {
		gridFs = new GridFS(gridfsDb);
	}

	@Override
	public List<UploadReceiptImage> getAllObjects() {
		List<UploadReceiptImage> list = new ArrayList<UploadReceiptImage>();
		DBCursor dbCursor = gridFs.getFileList();
		while(dbCursor.hasNext()) {
			DBObject dbObject = dbCursor.next();
			gridFs.find(dbObject.toString());
		}
		throw new UnsupportedOperationException("Method not implemented");
	}

    @Override
	public int getSize() {
		return gridFs.getFileList().size();
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void save(UploadReceiptImage object) throws Exception {
		persist(object);
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public String saveFile(UploadReceiptImage object) throws IOException {
		return persist(object);
	}

	@Override
	public UploadReceiptImage findOne(String id) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public WriteResult updateObject(String id, String name) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void delete(UploadReceiptImage object) {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void deleteObject(String id) {
		log.debug("deleted GridFs object - " + id);
		gridFs.remove(new ObjectId(id));
	}

	@Override
    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void createCollection() {
		throw new UnsupportedOperationException("Method not implemented");
	}

	@Override
	public void dropCollection() {
		throw new UnsupportedOperationException("Method not implemented");
	}

    @Transactional(readOnly = false, propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	private String persist(UploadReceiptImage uploadReceiptImage) throws IOException {
		boolean closeStreamOnPersist = true;
		GridFSInputFile receiptBlob = gridFs.createFile(uploadReceiptImage.getFileData().getInputStream(),
                uploadReceiptImage.getFileName(),
                closeStreamOnPersist);

		receiptBlob.setContentType(uploadReceiptImage.getFileData().getContentType());
        receiptBlob.setMetaData(uploadReceiptImage.getMetaData());

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

    @Override
    public long collectionSize() {
        return getSize();
    }
}
