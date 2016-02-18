$ ->
  $.get "/productores", (productores) ->
    $.each productores, (index, productor) ->
      nombre = $("<td>").text productor.nombre
      carnet = $("<td>").text productor.carnet
      telefono = $("<td>").text productor.telefono
      direccion = $("<td>").text productor.direccion
      cuenta = $("<td>").text productor.cuenta
      asociacion = $("<td>").text productor.asociacion
      $("#productores").append $("<tr>").append(nombre).append(carnet).append(telefono).append(direccion).append(cuenta).append(asociacion)