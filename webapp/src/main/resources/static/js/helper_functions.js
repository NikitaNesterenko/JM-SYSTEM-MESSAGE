function convert_date_to_format_Json(date) {
    let mm = date.getMonth() + 1;
    let dd = date.getDate();
    let hh = date.getHours();
    let min = date.getMinutes();

    if (mm < 10) {
        mm = '0' + mm;
    }
    if (dd < 10) {
        dd = '0' + dd;
    }
    if (hh < 10) {
        hh = '0' + hh;
    }
    if (min < 10) {
        min = '0' + min;
    }
    const dateStr = dd + '.' + mm + '.' + date.getFullYear();
    const timeStr = hh + ':' + min;

    return dateStr+' '+timeStr;
}