$ ->
  $.get "/storekeepers", (rows) ->
    $.each rows, (index, row) ->
      nombre = $("<td>").text row.nombre
      carnet = $("<td>").text row.carnet
      telefono = $("<td>").text row.telefono
      direccion = $("<td>").text row.direccion
      sueldo = $("<td>").text row.sueldo
      links = $("<td>").html '<a href="/storekeeper_update/' + row.id + '"><span class="glyphicon glyphicon-pencil">Editar</span></a>' + '<a href="/storekeeper_remove/' + row.id + '"><span class="glyphicon glyphicon-remove">Eliminar</span></a>' + '<a href="/storekeeper_show/' + row.id + '"><span class="glyphicon glyphicon-remove">Mostrar</span></a>' + '<a href="/storekeeper_profile/' + row.id + '"><span class="glyphicon glyphicon-remove">Profile</span></a>'
      $("#rows").append $("<tr>").append(nombre).append(carnet).append(telefono).append(direccion).append(sueldo).append(links)