package com.citi.interview;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author viga
 */
public class Task04AsyncDataProviderTest {
    private final String PREFIX = "user";
    class NameService implements Task04AsyncDataProvider.Webservice {
        private final long timeout;
        
        public NameService(long timeout) {
            this.timeout = timeout;
        }
        
        @Override
        public String findUserName(String id) {
            try {
                TimeUnit.MILLISECONDS.sleep(timeout);
            } catch (InterruptedException ex) {
                ex.printStackTrace(System.err);
            }
            return PREFIX + id;
        }
    }
    
    public Task04AsyncDataProviderTest() {        
    }

    @Test
    public void testFindUserNameAsync() 
            throws InterruptedException, ExecutionException {
        int timeout = 1000;
        Task04AsyncDataProvider provider 
                = new Task04AsyncDataProvider(new NameService(timeout));
        CompletableFuture<String> returnVal = new CompletableFuture<>();
        String id = "1";
        provider.findUserNameAsync(id, timeout*2 , "not found",
                (String s) -> {
            returnVal.complete(s);
        });
        assertEquals(PREFIX+id, returnVal.get());
    }
    
    
    @Test
    public void testFindUserNameAsyncNegative() 
            throws InterruptedException, ExecutionException {
        int timeout = 1000;
        Task04AsyncDataProvider provider 
                = new Task04AsyncDataProvider(new NameService(timeout*2));
        CompletableFuture<String> returnVal = new CompletableFuture<>();
        String id = "1";
        provider.findUserNameAsync(id, timeout , "not found",
                (String s) -> {
            returnVal.complete(s);
        });
        assertNotEquals(PREFIX+id, returnVal.get());
    }
    
    @Test
    public void testFindUserNameAsyncSeveralNames() 
            throws InterruptedException, ExecutionException {
        int timeout = 1000;
        Task04AsyncDataProvider provider 
                = new Task04AsyncDataProvider(new NameService(timeout));
        CompletableFuture<String> returnVal1 = new CompletableFuture<>();
        CompletableFuture<String> returnVal2 = new CompletableFuture<>();
        String id_1 = "1", id_2 = "2";
        provider.findUserNameAsync(id_1, timeout , "not found",
                (String s) -> {
            returnVal1.complete(s);
        });
        provider.findUserNameAsync(id_2, timeout , "not found",
                (String s) -> {
            returnVal2.complete(s);
        });
        assertNotEquals(PREFIX+id_1, returnVal1.get());
        assertNotEquals(PREFIX+id_2, returnVal2.get());
    }
}
