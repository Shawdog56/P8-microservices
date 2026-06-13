function checkAuth() {
    const loginView = document.getElementById('loginView');
    const appView = document.getElementById('appView');
    const token = localStorage.getItem('userToken');

    if (token) {
        loginView.classList.add('hidden');
        appView.classList.remove('hidden');
    } else {
        appView.classList.add('hidden');
        loginView.classList.remove('hidden');
    }
}

checkAuth();


async function handleLogin() {
    const username = document.getElementById('usernameInput').value;
    const password = document.getElementById('passwordInput').value;
    const submitBtn = document.querySelector('#loginForm button');

    submitBtn.innerText = "Cargando...";
    submitBtn.disabled = true;

    try {
        const response = await fetch('/auth/api/v1/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (!response.ok) {
            throw new Error('Credenciales incorrectas o error de servidor.');
        }

        const data = await response.json();
        
        localStorage.setItem('userToken', data.token);
        
        document.getElementById('loginForm').reset();
        checkAuth();

    } catch (err) {
        alert(err.message);
    } finally {
        submitBtn.innerText = "Ingresar";
        submitBtn.disabled = false;
    }
}

function handleLogout() {
    localStorage.removeItem('userToken');
    checkAuth();
}


async function sendImages() {
    const fileInput = document.getElementById('imageInput');
    const task = document.getElementById('taskSelect').value;
    const files = fileInput.files;
    if (files.length === 0) return alert("Select images first");

    const token = localStorage.getItem('userToken');
    if (!token) {
        handleLogout();
        return;
    }

    $("#statusText").text("Leyendo archivos...");
    $("#progressContainer").fadeIn();
    $("#progressBar").css("width", "30%");

    const imageList = [];
    for (let i = 0; i < files.length; i++) {
        const result = await readFile(files[i]);
        imageList.push(result);
    }

    $("#statusText").text("Procesando lote en el servidor...");
    $("#progressBar").css("width", "60%");

    $.ajax({
        url: "/api/images/process-images?task=" + task,
        type: "POST",
        contentType: "application/json",
        data: JSON.stringify({ files: imageList }),
        xhrFields: { responseType: 'blob' },
        
        headers: {
            "Authorization": "Bearer " + token
        },
        
        success: function(blob) {
            $("#progressBar").css("width", "100%");
            
            const url = window.URL.createObjectURL(blob);
            const a = document.createElement("a");
            a.href = url;
            a.download = `processed_${task.toLowerCase()}_batch.zip`;
            a.click();
            
            $("#statusText").text("Proceso completado con éxito!");
            document.getElementById('multiImageForm').reset();
            
            setTimeout(() => {
                $("#progressContainer").fadeOut(() => {
                    $("#progressBar").css("width", "0%");
                    $("#statusText").text("Listo...");
                });
            }, 3000);
        },
        
        error: function(xhr) {
            $("#progressContainer").fadeOut();
            $("#progressBar").css("width", "0%");

            if (xhr.status === 401) {
                alert("Tu sesión ha expirado o no es válida. Por favor, inicia sesión de nuevo.");
                handleLogout();
            } else {
                alert("Hubo un error al procesar las imágenes en el servidor.");
                $("#statusText").text("Error en el procesamiento.");
            }
        }
    });
}

const readFile = (file) => new Promise((res) => {
    const r = new FileReader();
    r.onloadend = () => res({ filename: file.name, content: r.result.split(',')[1] });
    r.readAsDataURL(file);
});