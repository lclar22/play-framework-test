row_id = location.pathname.split('/')[2]

$ ->
  $.get "/transactionDetails/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      discountReport = $("<td>").text row.discountReport
      requestId = $("<td>").text row.requestId
      productorId = $("<td>").text row.productorId
      status = $("<td>").text row.status
      discount = $("<td>").text row.discount
      links = $("<td>").html '<a href="/transactionDetail_update/' + row.id + '" class="btn btn-primary">Editar</a>' + '<a href="/transactionDetail_remove/' + row.id + '" class="btn btn-danger">Eliminar</a>' + '<a href="/transactionDetail_show/' + row.id + '" class="btn btn-info">Mostrar</a>'
      $("#descuentos_rows").append $("<tr>").append(discountReport).append(productorId).append(status).append(discount).append(links)
