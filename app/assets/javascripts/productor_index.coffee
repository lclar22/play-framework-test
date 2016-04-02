$ ->
  $.get "/productores", (rows) ->
    $.each rows, (index, row) ->
      nombre = $("<td>").text row.nombre
      carnet = $("<td>").text row.carnet
      telefono = $("<td>").text row.telefono
      cuenta = $("<td>").text row.cuenta
      asociacion = $("<td>").text row.asociacion
      link = $("<td>").html '<a href="/productor_update/' + row.id + '" class="btn btn-primary">Editar</a>' + '<a href="/productor_remove/' + row.id + '" class="btn btn-danger">Eliminar</a>' + '<a href="/productor_show/' + row.id + '" class="btn btn-info">Mostrar</a>'
      $("#rows").append $("<tr>").append(nombre).append(carnet).append(telefono).append(cuenta).append(asociacion).append(link)