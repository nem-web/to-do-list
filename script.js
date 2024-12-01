
const inputBox = document.querySelector('#input-box');
const listContainer = document.querySelector('#list-container');
const themeMode = document.querySelector('#theme-mode');

function addTask(){
    if(inputBox.value === ''){
        alert("You must write something!");
    }
    else{
        let li = document.createElement('li');
        li.innerHTML = inputBox.value;
        listContainer.appendChild(li);
        let span = document.createElement('span');
        span.innerHTML = '&#10006;';
        li.appendChild(span);
    }
    inputBox.value = '';
    saveData();
}

listContainer.addEventListener('click', function(e){
    if(e.target.tagName === 'LI'){
        e.target.classList.toggle("checked");
        saveData();
    }
    else if(e.target.tagName === 'SPAN'){
        e.target.parentElement.remove();
        saveData();
    }
},false);

function saveData(){
    localStorage.setItem("data",listContainer.innerHTML);
}

function showTask(){
    listContainer.innerHTML = localStorage.getItem("data");
}

showTask();

function changeMode(){
    console.log(themeMode.src);
    if(themeMode.src.match('light')){
        themeMode.src = 'img/dark.png';
    }
    else{
        themeMode.src = 'img/light.png';
    }
}