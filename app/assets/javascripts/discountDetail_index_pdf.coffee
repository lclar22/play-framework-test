$ ->
  $.get "/discountDetails", (rows) ->
    $.each rows, (index, row) ->
      discountReport = $("<td>").text row.discountReport
      productorId = $("<td>").text row.productorId
      amount = $("<td>").text row.amount
      $("#rows").append $("<tr>").append(discountReport).append(productorId).append(amount)
