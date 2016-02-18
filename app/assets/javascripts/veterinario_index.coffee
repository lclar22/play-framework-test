$ ->
  $.get "/veterinarios", (veterinarios) ->
    $.each veterinarios, (index, veterinario) ->
      monto = $("<td>").text veterinario.monto
      cuenta = $("<td>").text veterinario.cuenta
      cliente = $("<td>").text veterinario.cliente
      $("#veterinarios").append $("<tr>").append(monto).append(cuenta).append(cliente)