$ ->
  $.get "/proveedores", (proveedores) ->
    $.each proveedores, (index, proveedor) ->
      monto = $("<td>").text proveedor.monto
      cuenta = $("<td>").text proveedor.cuenta
      cliente = $("<td>").text proveedor.cliente
      $("#proveedores").append $("<tr>").append(monto).append(cuenta).append(cliente)