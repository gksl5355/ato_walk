insert into products (name, category, price, stock_quantity, status, description, created_at)
select '튼튼 사료 2kg', 'FEED', 32000, 50, 'ON_SALE', '성견용 균형 영양 사료', now()
where not exists (
    select 1 from products where name = '튼튼 사료 2kg'
);

insert into products (name, category, price, stock_quantity, status, description, created_at)
select '바삭 닭가슴살 간식', 'SNACK', 8900, 120, 'ON_SALE', '훈련 보상용 저염 간식', now()
where not exists (
    select 1 from products where name = '바삭 닭가슴살 간식'
);

insert into products (name, category, price, stock_quantity, status, description, created_at)
select '노즈워크 장난감 볼', 'TOY', 15000, 80, 'ON_SALE', '후각 놀이용 퍼즐 토이', now()
where not exists (
    select 1 from products where name = '노즈워크 장난감 볼'
);
