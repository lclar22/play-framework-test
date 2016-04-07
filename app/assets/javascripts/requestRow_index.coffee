$ ->
  $.get "/requestRows", (rows) ->
    $.each rows, (index, row) ->
      requestId = $("<td>").text row.requestId
      productId = $("<td>").text row.productId
      productorId = $("<td>").text row.productorId
      quantity = $("<td>").text row.quantity
      cost = $("<td>").text row.cost
      paid = $("<td>").text row.paid
      status = $("<td>").text row.status
      links = $("<td>").html '<a href="/requestRow_cancel/' + row.id + '" class="btn btn-danger">Cancelar</a>' + '<a href="/requestRow_fill/' + row.id + '" class="btn btn-primary">Entregar</a>' + '<a href="/requestRow_update/' + row.id + '" class="btn btn-primary">Editar</a>' + '<a href="/requestRow_remove/' + row.id + '" class="btn btn-danger">Eliminar</a>' + '<a href="/requestRow_show/' + row.id + '" class="btn btn-info">Mostrar</a>'
      $("#rows").append $("<tr>").append(requestId).append(productId).append(productorId).append(quantity).append(cost).append(paid).append(status).append(links)
