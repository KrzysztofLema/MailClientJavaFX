package pl.krzysztofLema.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import pl.krzysztofLema.model.EmailAccountBean;
import pl.krzysztofLema.model.EmailMessageBean;
import pl.krzysztofLema.model.folder.EmailFolderBean;

import javax.mail.Folder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelAccess {


    private Map<String,EmailAccountBean> emailAccounts = new HashMap<String, EmailAccountBean>();
    private ObservableList<String> emailAccoutsNames = FXCollections.observableArrayList();


    public ObservableList<String> getEmailAccoutsNames() {
        return emailAccoutsNames;
    }

    public EmailAccountBean getEmailAccountByName(String name){
        return emailAccounts.get(name);
    }
    public void addAccount(EmailAccountBean account){
        emailAccounts.put(account.getEmailAdress(),account);
        emailAccoutsNames.add(account.getEmailAdress());
    }

    private EmailMessageBean selectedMessage;

    public EmailMessageBean getSelectedMessage() {
        return selectedMessage;
    }

    public void setSelectedMessage(EmailMessageBean selectedMessage) {
        this.selectedMessage = selectedMessage;
    }

    private EmailFolderBean<String> selectedFolder;

    public EmailFolderBean<String> getSelectedFolder() {
        return selectedFolder;
    }

    public void setSelectedFolder(EmailFolderBean<String> selectedFolder) {
        this.selectedFolder = selectedFolder;
    }

    private List<Folder> foldersList = new ArrayList<Folder>();

    public List<Folder> getFolderList(){
        return foldersList;
    }

    public void addFolder(Folder folder){
        foldersList.add(folder);
    }
}
