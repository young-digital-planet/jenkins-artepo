package org.jenkinsci.plugins.artepo.repo;

import hudson.FilePath;
import org.jenkinsci.plugins.artepo.CopyPattern;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;

public interface Repo extends Serializable {

    /**
     * Called on source repository to prepare files to be copied to destination repository.
     *
     * @param buildTag Tag of previously copied build
     * @return Directory with files prepared to copy
     *
     * @throws InterruptedException
     * @throws IOException
     */
    abstract public FilePath prepareSource(RepoInfoProvider infoProvider, String buildTag)
            throws InterruptedException, IOException;

    /**
     * Copy files from <code>source</code> to destination repository.
     *
     * In case SCMs it implies commit will be performed. Copy have to be tagged with <code>buildTag</code>.
     *
     * @param sourcePath Source directory of artifacts to be copied. It is a result from {@link #prepareSource(RepoInfoProvider,String)}
     *               called on source repository
     * @param pattern Patterns of files to be copied
     * @param buildTag Tag name of new copy

     * @throws InterruptedException
     * @throws IOException
     */
    abstract public void copyFrom(RepoInfoProvider infoProvider, FilePath sourcePath,
                                  CopyPattern pattern, String buildTag)
            throws InterruptedException, IOException;
}
