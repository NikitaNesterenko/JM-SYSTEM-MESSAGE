export class SearcherDataElements {

    data = null;
    text = null;
    dataResults;
    dataFilers;

    constructor() {
        this.dataResults = $('#searchDataResults');
        this.dataFilers = $('#searchDataFilters');
    }

    setData(data) {
        this.data = data;
    }

    clearResults() {
        this.dataResults.empty();
    }

    search(){
        //TODO search by filters
    }

    addDataMessage(name, content, text) {
        this.dataResults.append(
            `<div class="card" style="margin-bottom: 8px">
                    <div class="card-body">
                        <h5 class="card-title">` + name + `</h5>
                        <p class="card-text">` + content.replace(text, '<mark>' + text + '</mark>') + `</p>
                    </div>
                </div>`);
    }

    addDataFiler(elementId, name) {
        this.dataFilers.find('#' + elementId).find('dl').append(
            `<dd>
                <input type="checkbox">
                ` + name + `
            </dd>`
        );
    }
}