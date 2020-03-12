import {WorkspaceRestPaginationService} from "/js/rest/entities-rest-pagination.js";

export class SwitchWorkspaceWindow {

    constructor() {
        this.workspaceService = new WorkspaceRestPaginationService();
        this.switchWsBtn = $('#switchWorkspaceButton');
        this.switchWsWindow = $('#switchWorkspacesWindow');
        this.switchWsMenuBar = $('#switchWorkspaceMenuBar');
        this.current_workspace_id = this.getCurrentWorkspaceId();
    }

    getCurrentWorkspaceId() {
        this.workspaceService.getChosenWorkspace().then(
            workspace => {
                this.current_workspace_id = workspace.id
            });
    }

    showWindowWithAvailableWorkspaces() {
        this.onSwitchWorkspacesButtonHover();
        this.onSwitchWorkspacesWindowHover()
    }

    onSwitchWorkspacesButtonHover() {
        this.switchWsBtn.hover(function () {
            $('#switchWorkspacesWindow').show()
        }, function () {
            $('#switchWorkspacesWindow').hide()
        });
    }

    onSwitchWorkspacesWindowHover() {
        this.switchWsWindow.on('mouseout', function () {
            $('#switchWorkspacesWindow').hide()
        });
        this.switchWsWindow.on('mouseover', function () {
            $('#switchWorkspacesWindow').show()
        })
    }

    showAvailableWorkspaces() {
        this.workspaceService.getWorkspacesByLogged().then(
            workspaces => {
                $.each(workspaces, (idx, wks) => {
                    if (idx < 1) {
                        this.switchWsMenuBar.hide();
                    } else {
                        this.switchWsMenuBar.show();
                    }
                    if (wks.id !== this.current_workspace_id) {
                        this.addWorkspacesToWindow(wks.name)
                    }
                });
            }
        );
        return this;
    }

    addWorkspacesToWindow(workspace_name) {
        $('#workspaces').append(
            `<div class="c-menu_item__li" id="${workspace_name}">
                   <a role="menuitem" target="_self" class="c-link c-button-unstyled c-menu_item__button" tabindex="-1" rel="noopener noreferrer"
                   id="${workspace_name}">
                       <div class="c-menu_item__label" id="${workspace_name}">
                           <div class="p-global_nav__top_switch_workspaces_icon"></div>
                       <strong id="${workspace_name}">${workspace_name}</strong>
                       </div>
                    </a>
             </div>`
        );
    }

    changeWorkspaceOnClick() {
        $('#workspaces').on('click', (event) => {
            let workspace_name = event.target.id;
            this.goToWorkspace(workspace_name);
        });
    }

    goToWorkspace(workspace_name) {
        this.workspaceService.setChosenWorkspace(workspace_name).then(
            response => {
                if (response === true) {
                    window.location.href = '/workspace';
                }
            }
        )
    }

    buildEvents() {
        this.showWindowWithAvailableWorkspaces();
        this.showAvailableWorkspaces();
        this.changeWorkspaceOnClick()
    }
}



