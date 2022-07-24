function validateBookForm() {

    let r = document.forms["bookForm"]["receiptDate"].value;
    if (r  === "" || r === null) {
        alert("Выберите дату поступления книги");
        return false;
    }

    let a = document.forms["bookForm"]["authors"].value;
    if (a === "" || a === null) {
        alert("Выберите автора(ов) книги");
        return false;
    }

    let g = document.forms["bookForm"]["genres"].value;
    if (g === "" || g === null) {
        alert("Выберите жанр(ы) книги");
        return false;
    }

    let p = document.forms["bookForm"]["publisher"].value;
    if (p === "" || p === null) {
        alert("Выберите книжное издательство");
        return false;
    }

    let t = document.forms["bookForm"]["title"].value;
    if (t === "" || t === null) {
        alert("Введите название книги");
        return false;
    }

    let l = document.forms["bookForm"]["language"].value;
    if (l  === ""|| l === null) {
        alert("Введите язык книги");
        return false;
    }

    let y = document.forms["bookForm"]["yearOfPublishing"].value;
    const date = new Date();
    const year = date.getFullYear();
    if (y < 1000 || y > year || (typeof y !== 'number' && isNaN(y))) {
        alert("Введите год издания книги в формате 'гггг', например 2022, но не далее текущего года");
        return false;
    }
}