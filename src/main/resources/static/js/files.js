'use strict';

const multipleUploadForm = document.querySelector('#multipleUploadForm');
const multipleFileUploadInput = document.querySelector('#multipleFileUploadInput');
const multipleFileUploadError = document.querySelector('#multipleFileUploadError');
const multipleFileUploadSuccess = document.querySelector('#multipleFileUploadSuccess');

function uploadMultipleFiles(files) {
    const formData = new FormData();
    for(let index = 0; index < files.length; index++) {
        formData.append("files", files[index]);
    }

    const xhr = new XMLHttpRequest();
    xhr.open("POST", "/uploadMultipleFiles");

    xhr.onload = function() {
        console.log(xhr.responseText);
        const response = JSON.parse(xhr.responseText);
        if(xhr.status === 200) {
            multipleFileUploadError.style.display = "none";
            let content = "<p>Файл(ы) загружен(ы) успешно</p>";
            for(let i = 0; i < response.length; i++) {
                content += "<p>Загрузить <a href='" + response[i].fileDownloadUri + "' target='_blank'>" + response[i].fileName + "</a></p>";
            }
            multipleFileUploadSuccess.innerHTML = content;
            multipleFileUploadSuccess.style.display = "block";
        } else {
            multipleFileUploadSuccess.style.display = "none";
            multipleFileUploadError.innerHTML = (response && response.message) || "Произошла ошибка";
        }
    }

    xhr.send(formData);
}

multipleUploadForm.addEventListener('submit', function(event){
    const files = multipleFileUploadInput.files;
    if(files.length === 0) {
        multipleFileUploadError.innerHTML = "Выберите файл(ы)";
        multipleFileUploadError.style.display = "block";
    }
    uploadMultipleFiles(files);
    event.preventDefault();
}, true);