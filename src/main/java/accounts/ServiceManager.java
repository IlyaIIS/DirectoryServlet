package accounts;

public class ServiceManager {
    private static final AccountService accountService = new AccountService();
    public static AccountService getAccountService() {
        return accountService;
    }
}
