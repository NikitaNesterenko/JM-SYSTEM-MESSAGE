export class FileUploader {

    constructor() {
        this.selected_file = null;
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
}