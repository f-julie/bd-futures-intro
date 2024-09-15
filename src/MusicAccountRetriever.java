import futures.AmazonMusicAccount;
import futures.ImportAccountTask;
import futures.MusicAccountService;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class MusicAccountRetriever {

    private MusicAccountService accountService;

    /**
     * Constructor for MusicAccountRetriever.
     */
    public MusicAccountRetriever() {
        accountService = new MusicAccountService();
    }

    /**
     * Retrieves a list of AmazonMusicAccounts given a list of String account IDs.
     * PARTICIPANTS: Complete this method. It should submit an ImportAccountTask to the ExecutorService and return the
     *   Future&lt;AmazonMusicAccount&gt;.
     * @param accountIDs List of String account IDs.
     * @return List of imported AmazonMusicAccounts.
     */
    public List<AmazonMusicAccount> retrieveAccounts(List<String> accountIDs) {
        ExecutorService accountExecutor = Executors.newCachedThreadPool();
        List<AmazonMusicAccount> accountList = new ArrayList<>();

        List<Future<AmazonMusicAccount>> results = null;
        try {
            results = accountExecutor.invokeAll(generateImportTasks(accountIDs));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        for (Future<AmazonMusicAccount> result : results) {
            //PARTICIPANTS: replace the following line.
            //accountList.add(new AmazonMusicAccount("Null", 0, "Null"));

            try {
                // Retrieve the AmazonMusicAccount from the Future
                accountList.add(result.get());
            } catch (InterruptedException e) {
                System.out.println("MusicAccountStatsManager was interrupted.");
            } catch (ExecutionException e) {
                System.out.println("ImportAccountTask threw an exception.");
            }

        }

        accountExecutor.shutdown();

        return accountList;
    }
    
    private List<ImportAccountTask> generateImportTasks(List<String> accountIDs) {
        List<ImportAccountTask> tasks = new ArrayList<>();

        for (String id : accountIDs) {
            tasks.add(new ImportAccountTask(accountService, id));
        }

        return tasks;
    }
}
