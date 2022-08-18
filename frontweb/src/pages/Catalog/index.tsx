import ProductCard from 'components/ProductCard';
import { Product } from 'types/product';

const Catalog = () => {

  const product :Product = {
    id: 2,
    name: 'Smart TV',
    description: 'Lorem ipsum dolor sit amet consectetur adipisicing elit.Nisi quas eos beatae minima culpa perspiciatis',
    price: 2190.0,
    imgUrl: 'https://raw.githubusercontent.com/devsuperior/dscatalog-resources/master/backend/img/2-big.jpg',
    date: '2020-07-14T10:00:00Z',
    categories:[
      {
        id: 1,
        name: 'Livros'
      },
      {
        id: 3,
        name: 'Computadores'
      },
    ],
  };

  return (
    <div className="container my-4">
      <div className="row">
        <div className="col-sm-6 col-lg-4 col-xl-3">
          <ProductCard product={product} />
        </div>
        <div className="col-sm-6 col-lg-4 col-xl-3">
          <ProductCard product={product} />
        </div>
        <div className="col-sm-6 col-lg-4 col-xl-3">
          <ProductCard product={product} />
        </div>
        <div className="col-sm-6 col-lg-4 col-xl-3">
          <ProductCard product={product} />
        </div>
      </div>
    </div>
  );
};

export default Catalog;
