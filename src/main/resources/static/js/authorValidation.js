function validateAuthorForm() {

    let p = document.forms["authorForm"]["publishers"].value;
    if (p === "" || p === null) {
        alert("Выберите издательства");
        return false;
    }

    let s = document.forms["authorForm"]["surname"].value;
    if (s  === "" || s === null) {
        alert("Введите фамилию автора");
        return false;
    }

    let f = document.forms["authorForm"]["firstName"].value;
    if (f  === "" || f === null) {
        alert("Введите имя автора");
        return false;
    }

    let c = document.forms["authorForm"]["country"].value;
    if (c  === "" || c === null) {
        alert("Введите страну автора");
        return false;
    }
}