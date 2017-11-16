package pl.krzysztofLema.controller.services;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import pl.krzysztofLema.controller.ModelAccess;
import pl.krzysztofLema.model.EmailAccountBean;
import pl.krzysztofLema.model.EmailConstants;
import pl.krzysztofLema.model.folder.EmailFolderBean;

public class CreateAndRegisterEmailAccountService extends Service<Integer> {

    private String emailAdress;
    private String password;
    private EmailFolderBean<String> folderRoot;
    private ModelAccess modelAccess;

    public CreateAndRegisterEmailAccountService(String emailAdress, String password, EmailFolderBean<String> folderRoot, ModelAccess modelAccess) {
        this.emailAdress = emailAdress;
        this.password = password;
        this.folderRoot = folderRoot;
        this.modelAccess = modelAccess;
    }

    @Override
    protected Task<Integer> createTask() {

        return new Task<Integer>() {
            @Override
            protected Integer call() throws Exception {
                EmailAccountBean emailAccount = new EmailAccountBean(emailAdress, password);
                if (emailAccount.getLoginState() == EmailConstants.LOGIN_STATE_SUCCEDED) {
                    modelAccess.addAccount(emailAccount);
                    EmailFolderBean<String> emailFolderBean = new EmailFolderBean<String>(emailAdress);
                    folderRoot.getChildren().add(emailFolderBean);
                    FetchFoldersService fetchFoldersService = new FetchFoldersService(emailFolderBean,emailAccount,modelAccess);
                    fetchFoldersService.start();
                }
                return emailAccount.getLoginState();
            }
        };
    }
}
