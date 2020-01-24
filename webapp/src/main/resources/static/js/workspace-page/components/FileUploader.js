import {StorageService} from "/js/rest/entities-rest-pagination.js";

export class FileUploader {

    constructor() {
        this.selected_file = null;
        this.storage_service = new StorageService();
    }

    onProfileFileUpload() {
        this.selected_file = $("#FileUpload1");
        this.selected_file.change(function() {
            const filename = $(this).val().split('\\')[$(this).val().split('\\').length -1];
            $('#spnFilePath').html("<b>Selected Files: </b>" + filename);
        });
    }

    onAttachFileUpload() {
        this.selected_file = $("#file_selector");
        this.selected_file.change(function () {
            const fileName = $(this).val().split('\\')[$(this).val().split('\\').length - 1];
            let attachedFile = "";
            if (fileName.length > 0) {
                attachedFile = "<b>Attached file: </b>" + fileName;
            }
            $('#attached_file').html(attachedFile);
        });
    }

    async saveFile(file) {
        if (file !== undefined) {
            const data = new FormData();
            data.append("file", file);
            await this.storage_service.uploadFile(data);
            $('#attached_file').html("");
            return file.name;
        }
        return null;
    }
}