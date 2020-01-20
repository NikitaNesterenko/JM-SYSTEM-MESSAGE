

$(document).ready(() => {
    const emojiButton = document.querySelector("#inputEmojiButton");
    const picker = new EmojiButton({
        position: 'auto',
        autoHide: true,
        autoFocusSearch: true,
        showSearch: true,
        showRecents: true
    });

    let input = document.querySelector("#form_message_input");

    picker.on('emoji', emoji => {
        let inputStart = input.value.slice(0, input.selectionStart);
        let inputEnd = input.value.slice(input.selectionEnd);
        input.value = inputStart + emoji + inputEnd;
        $("#form_message_input").focus();
    });

    emojiButton.addEventListener('click', () => {
        picker.pickerVisible ? picker.hidePicker() : picker.showPicker(emojiButton);
    });
});