package pl.edu.pw.ee.pyskp.documentworkflow.dto;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by piotr on 31.12.16.
 */
public class TaskInfoDTO {
    private long id;
    private String name;
    private String description;
    private long projectId;
    private UserInfoDTO administrator;
    private Date creationDate;
    private Date modificationDate;
    private List<UserInfoDTO> participants;
    private FileMetadataDTO lastModifiedFile;
    private List<FileMetadataDTO> filesInfo;

    public int getNumberOfParticipants() {
        Set<UserInfoDTO> participants = this.participants.stream().collect(Collectors.toSet());
        participants.add(administrator);
        return participants.size();
    }

    public int getNumberOfFiles() {
        return filesInfo == null ? 0 : filesInfo.size();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public UserInfoDTO getAdministrator() {
        return administrator;
    }

    public void setAdministrator(UserInfoDTO administrator) {
        this.administrator = administrator;
    }

    public List<FileMetadataDTO> getFilesInfo() {
        return filesInfo;
    }

    public void setFilesInfo(List<FileMetadataDTO> filesInfo) {
        this.filesInfo = filesInfo;
    }

    public List<UserInfoDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<UserInfoDTO> participants) {
        this.participants = participants;
    }

    public long getProjectId() {
        return projectId;
    }

    public void setProjectId(long projectId) {
        this.projectId = projectId;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public FileMetadataDTO getLastModifiedFile() {
        return lastModifiedFile;
    }

    public void setLastModifiedFile(FileMetadataDTO lastModifiedFile) {
        this.lastModifiedFile = lastModifiedFile;
    }
}
