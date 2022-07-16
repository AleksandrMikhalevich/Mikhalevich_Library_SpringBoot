function checkAuthorsCheckbox() {

    const checkbox = document.getElementById("flexCheckDefault");
    const btn_submit = document.querySelector(".btn-outline-success");

    checkbox.addEventListener("change", () => {
        if (checkbox.checked) {
            btn_submit.removeAttribute("disabled");
        } else {
            btn_submit.setAttribute("disabled", true);
        }
    });
}
