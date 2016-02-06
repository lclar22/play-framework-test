$ ->
  $.get "/clientes", (clientes) ->
    $.each clientes, (index, cliente) ->
      nombre = $("<td>").text cliente.nombre
      carnet = $("<td>").text cliente.carnet
      id_asociacion = $("<td>").text cliente.id_asociacion
      $("#clientes").append $("<tr>").append(nombre).append(carnet).append(id_asociacion)