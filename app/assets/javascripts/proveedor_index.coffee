$ ->
  $.get "/proveedores", (rows) ->
    $.each rows, (index, row) ->
      nombre = $("<td>").text row.nombre
      telefono = $("<td>").text row.telefono
      direccion = $("<td>").text row.direccion
      contacto = $("<td>").text row.contacto
      cuenta = $("<td>").text row.cuenta
      links = $("<td>").html '<a href="/proveedor_update/' + row.id + '"><span class="glyphicon glyphicon-pencil">Editar</span></a>' + '<a href="/proveedor_remove/' + row.id + '"><span class="glyphicon glyphicon-remove">Eliminar</span></a>' + '<a href="/proveedor_show/' + row.id + '"><span class="glyphicon glyphicon-remove">Mostrar</span></a>'
      $("#rows").append $("<tr>").append(nombre).append(telefono).append(direccion).append(contacto).append(cuenta).append(links)
