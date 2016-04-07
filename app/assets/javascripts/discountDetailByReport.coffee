row_id = location.pathname.split('/')[2]

$ ->
  $.get "/discountDetailsByReport/" + row_id, (rows) ->
    $.each rows, (index, row) ->
      discountReport = $("<td>").text row.discountReport
      requestId = $("<td>").text row.requestId
      productorId = $("<td>").text row.productorId
      status = $("<td>").text row.status
      amount = $("<td>").text row.amount
      links = $("<td>").html '<a href="/discountDetail_update/' + row.id + '" class="btn btn-primary">Editar</a>' + '<a href="/discountDetail_remove/' + row.id + '" class="btn btn-danger">Eliminar</a>' + '<a href="/discountDetail_show/' + row.id + '" class="btn btn-info">Mostrar</a>'
      $("#rows").append $("<tr>").append(discountReport).append(productorId).append(status).append(amount).append(links)
