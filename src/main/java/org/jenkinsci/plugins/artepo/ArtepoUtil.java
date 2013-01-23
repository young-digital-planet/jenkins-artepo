package org.jenkinsci.plugins.artepo;

import hudson.FilePath;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Sync;
import org.apache.tools.ant.types.FileSet;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class ArtepoUtil {
    static public final String PROMOTED_NUMBER = "PROMOTED_NUMBER";
    static public final String PLUGIN_NAME = "artepo";

    static public File toFile(FilePath filePath) {
        return new File(filePath.getRemote());
    }

    static public void sync(FilePath dst, FilePath src, Collection<SourcePattern> items) throws IOException, InterruptedException {
        try {
            Sync syncTask = new Sync();
            syncTask.setProject(new Project());
            syncTask.init();
            syncTask.setTodir(toFile(dst));
            syncTask.setOverwrite(true);
            syncTask.setIncludeEmptyDirs(false);

            if (items==null || items.isEmpty()) {
                items = new ArrayList<SourcePattern>(1);
                items.add(new SourcePattern(null, null, null));
            }

            for(SourcePattern item : items) {
                FilePath itemSrc = item.getSubFolder()==null||item.getSubFolder().length()==0 ? src : src.child(item.getSubFolder());
                String includes = item.getIncludes();
                FileSet fs = hudson.Util.createFileSet(toFile(itemSrc),
                        includes == null ? "" : includes,
                        item.getExcludes());
                fs.setDefaultexcludes(true);
                syncTask.addFileset(fs);
            }

            Sync.SyncTarget preserveInDst = new Sync.SyncTarget();
            String[] defaultExcludes = DirectoryScanner.getDefaultExcludes();
            for (String defaultExclude : defaultExcludes) {
                preserveInDst.createInclude().setName(defaultExclude);
            }
            preserveInDst.setDefaultexcludes(false);
            syncTask.addPreserveInTarget(preserveInDst);
            syncTask.setIncludeEmptyDirs(true);

            syncTask.execute();

        } catch (BuildException e) {
            throw new IOException("Failed to sync " + src + "/" + items + " to " + dst, e);
        }
    }

    static public void deleteRecursive(File file) throws IOException, InterruptedException {
        new FilePath(file).deleteRecursive();
    }
}
