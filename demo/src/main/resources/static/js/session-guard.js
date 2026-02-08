//SESSION CONFIG
const SESSION_TIMEOUT = 10 * 60 * 1000;  // 10 min incativity
const WARNING_TIME = 60 * 1000;         // warning for 1 min

let inactivityTimer;
let warningTimer;

// CREATE TIMEOUT UI
const warningBox = document.createElement("div");
warningBox.style.position = "fixed";
warningBox.style.bottom = "20px";
warningBox.style.right = "20px";
warningBox.style.padding = "15px 20px";
warningBox.style.background = "#1A1A2E";
warningBox.style.color = "#fff";
warningBox.style.border = "2px solid #fff";
warningBox.style.display = "none";
warningBox.style.zIndex = "9999";
warningBox.innerText = "⚠ Session expiring soon...";
document.body.appendChild(warningBox);

//SESSION CHECK
function checkSession(){
    fetch("/auth/session")
        .then(res => {
            if(!res.ok){
                window.location.href="/login.html";
            }
        })
        .catch(()=> window.location.href="/login.html");
}

// LOGOUT
function logoutUser(){
    fetch("/auth/logout",{method:"POST"})
        .then(()=> window.location.href="/login.html");
}

// RESET TIMER
function resetInactivityTimer(){

    clearTimeout(inactivityTimer);
    clearTimeout(warningTimer);

    warningBox.style.display="none";

    // show warning before logout
    warningTimer = setTimeout(()=>{
        warningBox.style.display="block";
        startCountdown();
    }, SESSION_TIMEOUT - WARNING_TIME);

    inactivityTimer = setTimeout(()=>{
        logoutUser();
    }, SESSION_TIMEOUT);
}

//COUNTDOWN DISPLAY
function startCountdown(){
    let remaining = WARNING_TIME / 1000;

    const interval = setInterval(()=>{
        remaining--;
        warningBox.innerText = ` Logging out in ${remaining}s due to inactivity`;

        if(remaining <= 0){
            clearInterval(interval);
        }
    },1000);
}

//  ACTIVITY LISTENERS
["click","mousemove","keydown","scroll"].forEach(event=>{
    document.addEventListener(event, resetInactivityTimer);
});

//  INITIALIZE
checkSession();
resetInactivityTimer();
