// Call the dataTables jQuery plugin
$(document).ready(function() {

    cargarUsuarios()

  $('#usuarios').DataTable();
  checkAuthentication();
  actualizarEmailDelUsuario();

});

function checkAuthentication() {
    const token = localStorage.token;
    if (!token) {
        // Si no hay token, redirigir al inicio de sesión
        alert("No se ha iniciado sesión!");
        window.location.href = 'login.html'
    }
}

function logout() {
    // Eliminar el token del localStorage
    localStorage.removeItem('token'); // Asegúrate de usar la clave que usaste para guardar el token
    localStorage.removeItem('email');
    localStorage.removeItem('nombre');
    // Redirigir al usuario a la página de inicio de sesión o a otra página

      window.location.href = 'login.html'
}

function actualizarEmailDelUsuario() {
    document.getElementById('txt-name-usuario').outerHTML = localStorage.nombre;
}

async function cargarUsuarios() {

  const request = await fetch('api/usuarios', {
    method: 'GET',
    headers: getHeaders()
  });
  const usuarios = await request.json();

  console.log(usuarios);

  let listadoHtml = ""

  for (let usuario of usuarios){

  let botonEliminar = '<a href="#" onclick = eliminarUsuario(' + usuario.id +
                      ') class="btn btn-danger"><i class="fas fa-trash"> Eliminar</i></a>';

  let botonEditar = '<a onclick = cargarUsuario(' + usuario.id +
                    ') href="#"  class="btn btn-success">Editar</a>';


  let usuarioHtml = '<tr><td>' + usuario.id + '</td><td>' + usuario.nombre + ' '
                    + usuario.apellido + '</td><td>' + usuario.email + '</td><td>'
                    + usuario.telefono +
                    '</td><td>' + botonEliminar + '     ' + botonEditar + '</td></tr>';

  listadoHtml += usuarioHtml;
  }
  document.querySelector('#usuarios tbody').outerHTML = listadoHtml;

}

async function cargarUsuario(id) {
    if (!confirm('¿Desea editar este usuario?')) {
        return;
    }


    const request = await fetch('api/usuario/' + id, {
        method: 'GET',
        headers: getHeaders()
    });

    const response = await request.json();

    console.log(response)

    if (!response || Object.keys(response).length === 0) {
        alert("No se encontró información para este usuario.");
        return;
    }


    document.getElementById('txtId').value = response.id;
    document.getElementById('txtNombre').value = response.nombre;
    document.getElementById('txtApellido').value = response.apellido;
    document.getElementById('txtTelefono').value = response.telefono;
    document.getElementById('txtEmail').value = response.email;

    const form = formulario();
    form.scrollIntoView({ behavior: 'smooth' });

}

function formulario() {
    return document.querySelector('.user');
}


async function actualizar() {
    let datos = {};
    datos.id = document.getElementById('txtId').value;
    datos.nombre = document.getElementById('txtNombre').value;
    datos.apellido = document.getElementById('txtApellido').value;
    datos.telefono = document.getElementById('txtTelefono').value;
    datos.email = document.getElementById('txtEmail').value;
    datos.password = document.getElementById('txtPassword').value;

    let repetirPassword = document.getElementById('txtRepetirPassword').value;

    if (repetirPassword !== datos.password) {
      alert('La contraseña que escribiste es diferente.');
      return;
    }

    try {
      const request = await fetch('api/usuario', {
        method: 'PUT',
        headers: getHeaders(),
        body: JSON.stringify(datos)
      });


      if (!request.ok) {

        if (request.status === 401) {
          alert('Token inválido. Por favor, inicia sesión nuevamente.');
          logout();
        } else if (request.status === 404) {
          alert('Usuario no encontrado. Por favor, verifica el ID.');
        } else {

          const errorResponse = await request.json();
          alert('Error al actualizar el usuario: ' + errorResponse);
        }
      } else {
        alert("Usuario actualizado con éxito!");

        const form = formulario();
        form.reset();

        cargarUsuarios();
      }
    } catch (error) {

      alert('Error al realizar la solicitud: ' + error.message);
    }
  }



function getHeaders() {
    return {
     'Accept': 'application/json',
     'Content-Type': 'application/json',
     'Authorization': localStorage.token
   };
}

async function eliminarUsuario(id) {

  if (!confirm('¿Desea eliminar este usuario?')) {
      return;
    }

 const request = await fetch('api/usuario/' + id, {
    method: 'DELETE',
    headers: getHeaders()

  });

cargarUsuarios()
}