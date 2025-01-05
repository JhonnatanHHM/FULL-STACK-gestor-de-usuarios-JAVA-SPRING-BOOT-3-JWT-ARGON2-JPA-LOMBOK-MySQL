$(document).ready(function() {
   // on ready
});

async function iniciarSesion() {
  let datos = {};
  datos.email = document.getElementById('txtEmail').value;
  datos.password = document.getElementById('txtPassword').value;

  const request = await fetch('api/login', {
    method: 'POST',
    headers: {
      'Accept': 'application/json',
      'Content-Type': 'application/json'
    },
    body: JSON.stringify(datos)
  });

  const respuesta = await request.text();

  if (respuesta !== 'FAIL') {
    // Decodificar el token para extraer el nombre
    const tokenData = jwt_decode(respuesta);

    // Guardar los valores en localStorage
    localStorage.token = respuesta;
    localStorage.email = tokenData.sub; // Extrae el email desde el token
    localStorage.nombre = tokenData.name; // Extrae el nombre desde el token

    window.location.href = 'users.html';
  } else {
    alert("Las credenciales son incorrectas. Por favor intente nuevamente.");
  }
}
