$ ->
  $.get "/insumos", (insumos) ->
    $.each insumos, (index, insumo) ->
      monto = $("<td>").text insumo.monto
      cuenta = $("<td>").text insumo.cuenta
      cliente = $("<td>").text insumo.cliente
      $("#insumos").append $("<tr>").append(monto).append(cuenta).append(cliente)