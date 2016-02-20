$ ->
  $.get "/proveedores", (proveedores) ->
    $.each proveedores, (index, proveedor) ->
      nombre = $("<td>").text proveedor.nombre
      telefono = $("<td>").text proveedor.telefono
      direccion = $("<td>").text proveedor.direccion
      contacto = $("<td>").text proveedor.contacto
      cuenta = $("<td>").text proveedor.cuenta
      $("#proveedores").append $("<tr>").append(nombre).append(telefono).append(direccion).append(contacto).append(cuenta)
