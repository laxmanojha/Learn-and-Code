public class CustomerSearch
{
    public List<Customer> SearchByCountry(string country)
    {
        var query = from customer in db.customers
                    where customer.Country.Contains(country)
                    orderby customer.CustomerID ascending
                    select customer;

        return query.ToList();
    }

    public List<Customer> SearchByCompanyName(string company)
    {
        var query = from customer in db.customers
                    where customer.CompanyName.Contains(company)
                    orderby customer.CustomerID ascending
                    select customer;

        return query.ToList();
    }

    public List<Customer> SearchByContact(string contact)
    {
        var query = from customer in db.customers
                    where customer.Contact.Contains(contact)
                    orderby customer.CustomerID ascending
                    select customer;

        return query.ToList();
    }
}

public class CustomerDataFormat {
    public string ExportToCSV(List<Customer> customerData)
    {
        StringBuilder sb = new StringBuilder();

        foreach (var row in customerData)
        {
            sb.AppendFormat("{0},{1},{2},{3}", row.CustomerID, row.CompanyName, row.ContactName, row.Country);
            sb.AppendLine();
        }

        return sb.ToString();
    }
}
