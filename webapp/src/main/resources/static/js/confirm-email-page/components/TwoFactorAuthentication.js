import {SendCode} from "/js/ajax/createWorkspaceRestController/sendCode.js";

export class TwoFactorAuthentication {

    constructor({root}) {
        this.root = $(root);
    }

    setInputEvents() {
        let result = [];
        this.root.each(function (idx) {
            let $this = $(this);
            $this.data("maxlength", $this.prop("maxlength"));
            $this.data("index", idx);
            $this.removeAttr("maxlength");
        }).bind("input", this, function (event) {
            let $this = $(this);
            event.data.spill($this, $this.val());
            result[$this.data("index")] = $this.val();
            if (event.data.checkIfEmpty() !== false && result.length === 6) {
                event.data.sendCode(result.join(""));
            }
        });
    }

    spill(inputBox, val) {
        let maxlength = inputBox.data("maxlength");
        if (val.length >= maxlength) {
            inputBox.val(val.substring(0, maxlength));
            let next = inputBox.next("input").focus();
            this.spill(next, val.substring(maxlength));
        } else {
            inputBox.val(val);
        }
    }

    checkIfEmpty() {
        let arrayInputs = this.root.map(function () {
            return this.value;
        }).get();

        return !arrayInputs.includes("");
    }

    sendCode(code) {
        const sender = new SendCode(code);
        if (sender.sendCode() === null) {
            alert("ne tot kod :)");
        } else {
            setTimeout(function () {
                window.location.href = "/workspacename";
            }, 1000);
        }
    }
}