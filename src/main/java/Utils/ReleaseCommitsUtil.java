package Utils;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import Entity.Release;
import org.eclipse.jgit.revwalk.RevCommit;

public class ReleaseCommitsUtil {

    //This private constructor is meant to hide the public one: utility classes do not have to be instantiated.
    private ReleaseCommitsUtil() {
        throw new IllegalStateException("This class does not have to be instantiated.");
    }

    private static RevCommit getLastCommit(List<RevCommit> commitsList) {

        RevCommit lastCommit = commitsList.get(0);
        for(RevCommit commit : commitsList) {
            //if commitDate > lastCommitDate then refresh lastCommit
            if(commit.getCommitterIdent().getWhen().after(lastCommit.getCommitterIdent().getWhen())) {
                lastCommit = commit;

            }

        }
        return lastCommit;

    }

    /*Callers:
     * getRelCommAssociations (RetrieveGitInfo)
     * getCurrentClasses (RetrieveGitInfo)*/
    public static ReleaseCommits getCommitsOfRelease(List<RevCommit> commitsList, Release release, Date firstDate) throws ParseException {

        List<RevCommit> matchingCommits = new ArrayList<>();
        Date lastDate = release.getDate();

        for(RevCommit commit : commitsList) {
            Date commitDate = commit.getCommitterIdent().getWhen();

            //if firstDate < commitDate <= lastDate then add the commit in matchingCommits list
            if(commitDate.after(firstDate) && (commitDate.before(lastDate) || commitDate.equals(lastDate))) {
                matchingCommits.add(commit);

            }

        }
        RevCommit lastCommit = getLastCommit(matchingCommits);

        return new ReleaseCommits(release, matchingCommits, lastCommit);

    }

    /*Callers:
     * doLabeling (RetrieveGitInfo)
     * assignCommitsToClasses (RetrieveGitInfo)*/
    public static Release getReleaseOfCommit(RevCommit commit, List<ReleaseCommits> relCommAssociations) {
        for(ReleaseCommits relComm : relCommAssociations) {
            for(RevCommit c : relComm.getCommits()) {
                if(c.equals(commit)) {
                    return relComm.getRelease();
                }

            }

        }
        return null;

    }

}
