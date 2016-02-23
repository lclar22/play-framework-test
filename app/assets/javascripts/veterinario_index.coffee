$ ->
  $.get "/veterinarios", (veterinarios) ->
    $.each veterinarios, (index, veterinario) ->
      nombre = $("<td>").text veterinario.nombre
      carnet = $("<td>").text veterinario.carnet
      telefono = $("<td>").text veterinario.telefono
      direccion = $("<td>").text veterinario.direccion
      sueldo = $("<td>").text veterinario.sueldo
      $("#veterinarios").append $("<tr>").append(nombre).append(carnet).append(telefono).append(direccion).append(sueldo)