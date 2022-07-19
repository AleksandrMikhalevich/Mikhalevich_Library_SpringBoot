function validateForm() {

    let r = document.forms["addBook"]["receiptDate"].value;
    if (r  === "" || r === null) {
        alert("Выберите дату поступления книги");
        return false;
    }

    let a = document.forms["addBook"]["authors"].value;
    if (a === "" || a === null) {
        alert("Выберите автора(ов) книги");
        return false;
    }

    let g = document.forms["addBook"]["genres"].value;
    if (g === "" || g === null) {
        alert("Выберите жанр(ы) книги");
        return false;
    }

    let p = document.forms["addBook"]["publisher"].value;
    if (p === "" || p === null) {
        alert("Выберите книжное издательство");
        return false;
    }

    let t = document.forms["addBook"]["title"].value;
    if (t === "" || t === null) {
        alert("Введите название книги");
        return false;
    }

    let l = document.forms["addBook"]["language"].value;
    if (l  === ""|| l === null) {
        alert("Введите язык книги");
        return false;
    }

    let y = document.forms["addBook"]["yearOfPublishing"].value;
    const date = new Date();
    const year = date.getFullYear();
    if (y < 1000 || y > year || (typeof y !== 'number' && isNaN(y))) {
        alert("Введите год издания книги в формате 'гггг', например 2022, но не далее текущего года");
        return false;
    }
}