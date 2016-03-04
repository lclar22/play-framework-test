$ ->
  $.get "/productores", (productores) ->
    $.each productores, (index, productor) ->
      nombre = $("<td>").text productor.nombre
      carnet = $("<td>").text productor.carnet
      telefono = $("<td>").text productor.telefono
      cuenta = $("<td>").text productor.cuenta
      asociacion = $("<td>").text productor.asociacion
      link = $("<td>").html '<a href="/productor_update/' + productor.id + '"><span class="glyphicon glyphicon-pencil">Editar</span></a>' + '<a href="/productor_remove/' + productor.id + '"><span class="glyphicon glyphicon-remove">Eliminar</span></a>' + '<a href="/productor_show/' + productor.id + '"><span class="glyphicon glyphicon-remove">Mostrar</span></a>'
      $("#productores").append $("<tr>").append(nombre).append(carnet).append(telefono).append(cuenta).append(asociacion).append(link)