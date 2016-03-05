$ ->
  $.get "/productores", (rows) ->
    $.each rows, (index, row) ->
      nombre = $("<td>").text row.nombre
      carnet = $("<td>").text row.carnet
      telefono = $("<td>").text row.telefono
      cuenta = $("<td>").text row.cuenta
      asociacion = $("<td>").text row.asociacion
      link = $("<td>").html '<a href="/productor_update/' + row.id + '"><span class="glyphicon glyphicon-pencil">Editar</span></a>' + '<a href="/productor_remove/' + row.id + '"><span class="glyphicon glyphicon-remove">Eliminar</span></a>' + '<a href="/productor_show/' + row.id + '"><span class="glyphicon glyphicon-remove">Mostrar</span></a>'
      $("#rows").append $("<tr>").append(nombre).append(carnet).append(telefono).append(cuenta).append(asociacion).append(link)