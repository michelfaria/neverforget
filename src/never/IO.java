package never;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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
        if (NoteWindow.setnwinNoteWindows == null) {
            throw new IllegalStateException("Note set is null");
        }

        if (leSaveErrors == null) {
            leSaveErrors = new ArrayList<Exception>();
        }
        synchronized (leSaveErrors) {
            leSaveErrors = new ArrayList<Exception>();
            final CreateSaveDirStatus iResult = createSaveDirIfNotExists();
            if (iResult.equals(CreateSaveDirStatus.CREATION_FAIL)) {
                return NoteSaveStatus.FAIL_NO_SAVE_DIR;
            }
            for (final NoteWindow nw : NoteWindow.setnwinNoteWindows) {
                if (!nw.isContentsUnsaved()) {
                    continue;
                }
                FileOutputStream fous = null;
                try {
                    final File fSaveFile = new File(
                            NeverForget.C_F_SAVE_DIR.getCanonicalPath() + "/" + nw.getUUID().toString());
                    fous = new FileOutputStream(fSaveFile);
                    final String strData = String.format("%d,%d,%d,%d,%s", nw.getPosX(), nw.getPosY(), nw.getWidth(),
                            nw.getHeight(), nw.getNote().getContents());
                    fous.write(strData.getBytes());
                    nw.setContentsUnsaved(false);
                    System.out.println("Saved");
                } catch (Exception e) {
                    leSaveErrors.add(e);
                    continue;
                } finally {
                    if (fous != null) {
                        try {
                            fous.close();
                        } catch (IOException e) {
                            leSaveErrors.add(e);
                        }
                    }
                }
            }
            return leSaveErrors.size() > 0 ? NoteSaveStatus.FAIL_WRITE_ERROR : NoteSaveStatus.SUCCESS;
        }
    }

    public static List<Exception> leLoadNotesErrors = new ArrayList<Exception>();

    /**
     * Load errors are temporarily written to IO.leLoadNotesErrors.
     */
    public static List<NoteWindow> loadSavedNotes() {
        if (leLoadNotesErrors == null) {
            leLoadNotesErrors = new ArrayList<Exception>();
        }

        synchronized (leLoadNotesErrors) {
            leLoadNotesErrors = new ArrayList<Exception>();

            if (!NeverForget.C_F_SAVE_DIR.exists()) {
                return new ArrayList<NoteWindow>();
            }

            final File[] aFiles = NeverForget.C_F_SAVE_DIR.listFiles();
            if (aFiles == null) {
                throw new RuntimeException("Save location is not a directory.");
            }

            final List<NoteWindow> nwlList = new ArrayList<NoteWindow>();
            for (final File file : aFiles) {
                try {
                    final byte[] baContents = readFile(file);
                    final String strContents = new String(baContents, StandardCharsets.UTF_8);
                    final String[] astrContents = strContents.split(",");
                    final NoteWindow nwLoaded = new NoteWindow();
                    // Load UUID
                    try {
                        nwLoaded.setUUID(UUID.fromString(file.getName()));
                    } catch (IllegalArgumentException e) {
                        leLoadNotesErrors.add(e);
                        continue;
                    }
                    // Load Positions and Widths
                    try {
                        final int iX = Integer.parseInt(astrContents[0]);
                        final int iY = Integer.parseInt(astrContents[1]);
                        final int iWidth = Integer.parseInt(astrContents[2]);
                        final int iHeight = Integer.parseInt(astrContents[3]);
                        nwLoaded.setDlgDialogBounds(iX, iY, iWidth, iHeight);
                    } catch (NumberFormatException e) {
                        leLoadNotesErrors.add(e);
                        continue;
                    }
                    // Load contents
                    final String strNoteContents = astrContents[4];
                    nwLoaded.setnNoteContents(strNoteContents);
                    nwLoaded.setContentsUnsaved(false);
                    nwlList.add(nwLoaded);
                } catch (IOException e) {
                    leLoadNotesErrors.add(e);
                }
            }
            return nwlList;
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
