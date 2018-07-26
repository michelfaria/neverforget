package never;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public final class IO {

    public static enum CreateSaveDirStatus {
        CREATED, ALREADY_EXISTS, CREATION_FAIL
    }

    public static CreateSaveDirStatus createSaveDirIfNotExists() {
        if (NeverForget.C_F_SAVE_DIR.exists()) {
            return CreateSaveDirStatus.ALREADY_EXISTS;
        }
        final boolean bCreated = NeverForget.C_F_SAVE_DIR.mkdirs();
        if (!bCreated) {
            return CreateSaveDirStatus.CREATION_FAIL;
        }
        return CreateSaveDirStatus.CREATED;
    }

    public static enum NoteSaveStatus {
        SUCCESS, FAIL_NO_SAVE_DIR, FAIL_WRITE_ERROR;
    }

    public static List<Exception> leSaveErrors = new ArrayList<Exception>();
    
    /**
     * Write errors saved temporarily to IO.leSaveErrors
     */
    public static NoteSaveStatus saveAllNotes() {
        if (NoteWindow.setnwNoteWindows == null) {
            throw new IllegalStateException("Note set is null");
        }

        synchronized (leSaveErrors) {
            leSaveErrors = new ArrayList<Exception>();
            final CreateSaveDirStatus iResult = createSaveDirIfNotExists();
            if (iResult.equals(CreateSaveDirStatus.CREATION_FAIL)) {
                return NoteSaveStatus.FAIL_NO_SAVE_DIR;
            }
            for (final NoteWindow nw : NoteWindow.setnwNoteWindows) {
                FileOutputStream fos = null;
                try {
                    final File fSaveFile = new File(NeverForget.C_F_SAVE_DIR.getCanonicalPath() + "/" + nw.getUUID().toString());
                    fos = new FileOutputStream(fSaveFile);
                    fos.write(nw.getNote().getContents().getBytes());
                } catch (Exception e) {
                    leSaveErrors.add(e);
                    continue;
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            leSaveErrors.add(e);
                        }
                    }
                }
            }
            return leSaveErrors.size() > 0 ? NoteSaveStatus.FAIL_WRITE_ERROR : NoteSaveStatus.SUCCESS;
        }
    }

    public static byte[] readFile(File file) throws IOException {
        ByteArrayOutputStream bos = null;
        InputStream is = null;
        try {
            byte[] baBuf = new byte[8192];
            bos = new ByteArrayOutputStream();
            is = new FileInputStream(file);
            int read = 0;
            while ((read = is.read(baBuf)) != -1) {
                bos.write(baBuf, 0, read);
            }
            return bos.toByteArray();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
