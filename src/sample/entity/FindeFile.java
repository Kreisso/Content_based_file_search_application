package sample.entity;

import java.util.Optional;

public class FindeFile {
    private FileType fileType;
    private String url;
    private Optional<String> parentUrl;

    public FileType getFileType() {
        return fileType;
    }

    public String getUrl() {
        return url;
    }

    public Optional<String> getParentUrl() {
        return parentUrl;
    }


    public static final class Builder {
        private FileType fileType;
        private String url;
        private Optional<String> parentUrl;

        private Builder() {
        }

        public Builder fileType(FileType fileType) {
            this.fileType = fileType;
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder parentUrl(String parentUrl) {
            this.parentUrl = Optional.ofNullable(parentUrl);
            return this;
        }

        public FindeFile build() {
            FindeFile file = new FindeFile();
            file.url = this.url;
            file.fileType = this.fileType;
            file.parentUrl = this.parentUrl;
            return file;
        }
    }
}
