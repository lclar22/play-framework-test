$ ->
  $.get "/productores", (productores) ->
    $.each productores, (index, productor) ->
      monto = $("<td>").text productor.monto
      cuenta = $("<td>").text productor.cuenta
      cliente = $("<td>").text productor.cliente
      $("#productores").append $("<tr>").append(monto).append(cuenta).append(cliente)