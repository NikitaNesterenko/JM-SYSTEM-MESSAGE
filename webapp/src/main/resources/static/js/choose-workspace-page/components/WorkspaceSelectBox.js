import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js"

export class WorkspaceSelectBox {

    constructor() {
        this.workspaceService = new WorkspaceRestPaginationService();
    }

    workspaceOptions() {
        this.workspaceService.getWorkspacesByLogged().then(
            workspaces => {
                $.each(workspaces, (idx, wks) => { this.setOption(wks.name) });
            }
        );
        return this;
    }

    setOption(option_name) {
        $('#favcity').append(
            $('<option id="select_workspace"/>').attr('value', option_name).text(option_name)
        );
    }

    watchChanges() {
        $('#favcity').change((event) => {
            let wks_name = event.target.value;
            alert(wks_name);
            this.selectWorkspace(wks_name);
        });
    }

    selectWorkspace(workspace_name) {
        this.workspaceService.setChoosedWorkspace(workspace_name).then(
            response => {
                if (response === true) {
                    window.location.href = '/workspace';
                }
            }
        )
    }
}