import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { LayoutWrapperComponent } from '../../shared/layout/layout-wrapper.component';
import { ProductService, Product } from '../../services/product.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, RouterModule, LayoutWrapperComponent, FormsModule],
  template: `
    <app-layout-wrapper pageTitle="Reports">
      <div class="p-6">
        <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
          <h1 class="text-2xl font-bold text-gray-900">Reports</h1>
          <p class="mt-1 text-gray-600">View system reports and analytics</p>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6 mb-6">
          <h2 class="text-xl font-semibold mb-4">Product Statistics</h2>
          <div *ngIf="stats; else loadingStats" class="grid grid-cols-2 md:grid-cols-4 gap-4">
            <div class="bg-blue-50 rounded-lg p-4 text-center">
              <div class="text-2xl font-bold">{{ stats.totalProducts }}</div>
              <div class="text-gray-600">Total Products</div>
            </div>
            <div class="bg-green-50 rounded-lg p-4 text-center">
              <div class="text-2xl font-bold">{{ stats.activeProducts }}</div>
              <div class="text-gray-600">Active</div>
            </div>
            <div class="bg-purple-50 rounded-lg p-4 text-center">
              <div class="text-2xl font-bold">{{ stats.featuredProducts }}</div>
              <div class="text-gray-600">Featured</div>
            </div>
            <div class="bg-yellow-50 rounded-lg p-4 text-center">
              <div class="text-2xl font-bold">{{ stats.averagePrice | number:'1.2-2' }}</div>
              <div class="text-gray-600">Avg Price</div>
            </div>
          </div>
          <ng-template #loadingStats>
            <div class="text-gray-400">Loading statistics...</div>
          </ng-template>
        </div>
        <div class="bg-white rounded-xl shadow-sm p-6">
          <h2 class="text-xl font-semibold mb-4 flex items-center justify-between">
            <span>Product List</span>
            <div class="flex gap-2">
              <button (click)="openExportModal()" class="bg-yellow-600 text-white px-4 py-2 rounded">Export to Excel</button>
              <button (click)="openAddProduct()" class="bg-green-600 text-white px-4 py-2 rounded">+ Add Product</button>
            </div>
          </h2>
          <form class="flex flex-wrap gap-2 mb-4 items-center" (ngSubmit)="onSearch()">
            <input [(ngModel)]="search" name="search" placeholder="Search products..." class="border rounded px-3 py-2" (keyup.enter)="onSearch()" />
            <select [(ngModel)]="category" name="category" (change)="onFilterChange()" class="border rounded px-3 py-2">
              <option value="">All Categories</option>
              <option *ngFor="let c of categories" [value]="c">{{ c }}</option>
            </select>
            <select [(ngModel)]="brand" name="brand" (change)="onFilterChange()" class="border rounded px-3 py-2">
              <option value="">All Brands</option>
              <option *ngFor="let b of brands" [value]="b">{{ b }}</option>
            </select>
            <select [(ngModel)]="sortBy" name="sortBy" (change)="onSortChange(sortBy)" class="border rounded px-3 py-2">
              <option value="name">Name</option>
              <option value="price">Price</option>
              <option value="stockQuantity">Stock</option>
              <option value="brand">Brand</option>
              <option value="category">Category</option>
            </select>
            <button type="button" (click)="toggleSortDir()" class="border rounded px-3 py-2">
              Sort: {{ sortDir === 'asc' ? 'Asc' : 'Desc' }}
            </button>
            <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded">Search</button>
          </form>
          <div *ngIf="products; else loadingProducts">
            <div class="overflow-x-auto">
              <table class="min-w-full divide-y divide-gray-200">
                <thead>
                  <tr>
                    <th class="px-4 py-2 text-left">Name</th>
                    <th class="px-4 py-2 text-left">Category</th>
                    <th class="px-4 py-2 text-left">Brand</th>
                    <th class="px-4 py-2 text-right">Price</th>
                    <th class="px-4 py-2 text-right">Stock</th>
                    <th class="px-4 py-2 text-center">Actions</th>
                  </tr>
                </thead>
                <tbody>
                  <tr *ngFor="let p of products">
                    <td class="px-4 py-2">{{ p.name }}</td>
                    <td class="px-4 py-2">{{ p.category }}</td>
                    <td class="px-4 py-2">{{ p.brand }}</td>
                    <td class="px-4 py-2 text-right">{{ p.price | currency }}</td>
                    <td class="px-4 py-2 text-right">{{ p.stockQuantity }}</td>
                    <td class="px-4 py-2 text-center">
                      <button (click)="openEditProduct(p)" class="text-blue-600 hover:underline mr-2">Edit</button>
                      <button (click)="deleteProduct(p)" class="text-red-600 hover:underline">Delete</button>
                    </td>
                  </tr>
                </tbody>
              </table>
            </div>
            <div class="mt-4 flex flex-wrap items-center justify-between gap-2">
              <div class="flex items-center gap-2">
                <label for="pageSize" class="text-sm">Rows per page:</label>
                <select id="pageSize" [(ngModel)]="size" (change)="onPageSizeChange(size)" class="border rounded px-2 py-1">
                  <option *ngFor="let s of pageSizes" [value]="s">{{ s }}</option>
                </select>
              </div>
              <div class="flex items-center gap-2">
                <button (click)="prevPage()" [disabled]="page === 0" class="px-3 py-1 rounded bg-gray-100">Prev</button>
                <span>Page {{ page + 1 }} of {{ totalPages }} ({{ totalItems }} items)</span>
                <button (click)="nextPage()" [disabled]="!hasMore" class="px-3 py-1 rounded bg-gray-100">Next</button>
              </div>
            </div>
          </div>
          <ng-template #loadingProducts>
            <div class="text-gray-400">Loading products...</div>
          </ng-template>
        </div>

        <!-- Product Modal -->
        <div *ngIf="showProductForm" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div class="bg-white rounded-xl shadow-lg p-8 w-full max-w-lg relative">
            <button (click)="closeProductForm()" class="absolute top-2 right-2 text-gray-400 hover:text-gray-700">&times;</button>
            <h3 class="text-xl font-bold mb-4">{{ editingProduct ? 'Edit Product' : 'Add Product' }}</h3>
            <form (ngSubmit)="saveProduct()" class="space-y-4">
              <div>
                <label class="block text-sm font-medium mb-1">Name</label>
                <input [(ngModel)]="productForm.name" name="name" required class="w-full border rounded px-3 py-2" />
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">Description</label>
                <textarea [(ngModel)]="productForm.description" name="description" class="w-full border rounded px-3 py-2"></textarea>
              </div>
              <div class="flex gap-2">
                <div class="flex-1">
                  <label class="block text-sm font-medium mb-1">Category</label>
                  <input [(ngModel)]="productForm.category" name="category" required class="w-full border rounded px-3 py-2" />
                </div>
                <div class="flex-1">
                  <label class="block text-sm font-medium mb-1">Brand</label>
                  <input [(ngModel)]="productForm.brand" name="brand" required class="w-full border rounded px-3 py-2" />
                </div>
              </div>
              <div class="flex gap-2">
                <div class="flex-1">
                  <label class="block text-sm font-medium mb-1">Price</label>
                  <input type="number" [(ngModel)]="productForm.price" name="price" required class="w-full border rounded px-3 py-2" />
                </div>
                <div class="flex-1">
                  <label class="block text-sm font-medium mb-1">Stock</label>
                  <input type="number" [(ngModel)]="productForm.stockQuantity" name="stockQuantity" required class="w-full border rounded px-3 py-2" />
                </div>
              </div>
              <div class="flex gap-2">
                <div class="flex-1">
                  <label class="block text-sm font-medium mb-1">SKU</label>
                  <input [(ngModel)]="productForm.sku" name="sku" class="w-full border rounded px-3 py-2" />
                </div>
                <div class="flex-1 flex items-center gap-2 mt-6">
                  <label class="text-sm font-medium">Active</label>
                  <input type="checkbox" [(ngModel)]="productForm.active" name="active" />
                  <label class="text-sm font-medium">Featured</label>
                  <input type="checkbox" [(ngModel)]="productForm.featured" name="featured" />
                </div>
              </div>
              <div class="flex gap-2 justify-end pt-2">
                <button type="button" (click)="closeProductForm()" class="px-4 py-2 border rounded">Cancel</button>
                <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded">{{ editingProduct ? 'Update' : 'Add' }}</button>
              </div>
            </form>
          </div>
        </div>

        <!-- Export Modal -->
        <div *ngIf="showExportModal" class="fixed inset-0 bg-black bg-opacity-40 flex items-center justify-center z-50">
          <div class="bg-white rounded-xl shadow-lg p-8 w-full max-w-lg relative">
            <button (click)="closeExportModal()" class="absolute top-2 right-2 text-gray-400 hover:text-gray-700">&times;</button>
            <h3 class="text-xl font-bold mb-4">Export Products to Excel</h3>
            <form (ngSubmit)="submitExport()" class="space-y-4">
              <div class="flex gap-2">
                <div class="flex-1">
                  <label class="block text-sm font-medium mb-1">Sort By</label>
                  <select [(ngModel)]="exportForm.sortBy" name="exportSortBy" class="w-full border rounded px-3 py-2">
                    <option value="name">Name</option>
                    <option value="price">Price</option>
                    <option value="stockQuantity">Stock</option>
                    <option value="brand">Brand</option>
                    <option value="category">Category</option>
                  </select>
                </div>
                <div class="flex-1">
                  <label class="block text-sm font-medium mb-1">Sort Direction</label>
                  <select [(ngModel)]="exportForm.sortDir" name="exportSortDir" class="w-full border rounded px-3 py-2">
                    <option value="asc">Ascending</option>
                    <option value="desc">Descending</option>
                  </select>
                </div>
              </div>
              <div>
                <label class="block text-sm font-medium mb-1">Search</label>
                <input [(ngModel)]="exportForm.search" name="exportSearch" class="w-full border rounded px-3 py-2" />
              </div>
              <div class="flex gap-2">
                <div class="flex-1">
                  <label class="block text-sm font-medium mb-1">Category</label>
                  <select [(ngModel)]="exportForm.category" name="exportCategory" class="w-full border rounded px-3 py-2">
                    <option value="">All Categories</option>
                    <option *ngFor="let c of categories" [value]="c">{{ c }}</option>
                  </select>
                </div>
                <div class="flex-1">
                  <label class="block text-sm font-medium mb-1">Brand</label>
                  <select [(ngModel)]="exportForm.brand" name="exportBrand" class="w-full border rounded px-3 py-2">
                    <option value="">All Brands</option>
                    <option *ngFor="let b of brands" [value]="b">{{ b }}</option>
                  </select>
                </div>
              </div>
              <div class="flex gap-2 justify-end pt-2">
                <button type="button" (click)="closeExportModal()" class="px-4 py-2 border rounded">Cancel</button>
                <button type="submit" class="bg-blue-600 text-white px-4 py-2 rounded">Export</button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </app-layout-wrapper>
  `,
  styles: []
})
export class ReportsComponent implements OnInit {
  stats: any;
  products: Product[] = [];
  page = 0;
  size = 10;
  hasMore = false;
  search = '';
  category = '';
  brand = '';
  sortBy = 'name';
  sortDir = 'asc';
  categories: string[] = [];
  brands: string[] = [];
  showProductForm = false;
  editingProduct: Product | null = null;
  productForm: Product = {
    id: 0,
    name: '',
    description: '',
    price: 0,
    category: '',
    brand: '',
    sku: '',
    stockQuantity: 0,
    active: true,
    featured: false
  };
  totalPages = 1;
  totalItems = 0;
  pageSizes = [5, 10, 20, 50];
  showExportModal = false;
  exportForm = {
    sortBy: 'name',
    sortDir: 'asc',
    search: '',
    category: '',
    brand: ''
  };

  constructor(private productService: ProductService) {}

  ngOnInit() {
    this.loadStats();
    this.loadProducts();
    this.loadCategoriesAndBrands();
  }

  loadStats() {
    this.productService.getProductStatistics().subscribe(data => this.stats = data);
  }

  loadCategoriesAndBrands() {
    // For demo, extract from stats if available
    this.productService.getProductStatistics().subscribe(data => {
      this.categories = data.categoryCount ? Object.keys(data.categoryCount) : [];
      this.brands = data.brandCount ? Object.keys(data.brandCount) : [];
    });
  }

  loadProducts() {
    const filter: any = {};
    if (this.search) filter.globalSearch = this.search;
    if (this.category) filter.category = this.category;
    if (this.brand) filter.brand = this.brand;
    this.productService.filterProducts(filter, this.page, this.size, this.sortBy, this.sortDir).subscribe(res => {
      this.products = res.content || [];
      this.totalPages = res.totalPages || 1;
      this.totalItems = res.totalElements || 0;
      this.hasMore = res.totalPages ? this.page < res.totalPages - 1 : false;
    });
  }

  onSearch() {
    this.page = 0;
    this.loadProducts();
  }

  onFilterChange() {
    this.page = 0;
    this.loadProducts();
  }

  onSortChange(sortBy: string) {
    this.sortBy = sortBy;
    this.page = 0;
    this.loadProducts();
  }

  toggleSortDir() {
    this.sortDir = this.sortDir === 'asc' ? 'desc' : 'asc';
    this.page = 0;
    this.loadProducts();
  }

  nextPage() {
    if (this.hasMore) {
      this.page++;
      this.loadProducts();
    }
  }

  prevPage() {
    if (this.page > 0) {
      this.page--;
      this.loadProducts();
    }
  }

  openAddProduct() {
    this.editingProduct = null;
    this.productForm = {
      id: 0,
      name: '',
      description: '',
      price: 0,
      category: '',
      brand: '',
      sku: '',
      stockQuantity: 0,
      active: true,
      featured: false
    };
    this.showProductForm = true;
  }

  openEditProduct(product: Product) {
    this.editingProduct = product;
    this.productForm = { ...product };
    this.showProductForm = true;
  }

  closeProductForm() {
    this.showProductForm = false;
    this.editingProduct = null;
  }

  saveProduct() {
    if (this.editingProduct) {
      this.productService.updateProduct(this.editingProduct.id, this.productForm).subscribe(() => {
        this.closeProductForm();
        this.loadProducts();
      });
    } else {
      this.productService.createProduct(this.productForm).subscribe(() => {
        this.closeProductForm();
        this.loadProducts();
      });
    }
  }

  deleteProduct(product: Product) {
    if (confirm(`Delete product "${product.name}"?`)) {
      this.productService.deleteProduct(product.id).subscribe(() => {
        this.loadProducts();
      });
    }
  }

  onPageSizeChange(newSize: number) {
    this.size = +newSize;
    this.page = 0;
    this.loadProducts();
  }

  openExportModal() {
    this.exportForm = {
      sortBy: this.sortBy,
      sortDir: this.sortDir,
      search: this.search,
      category: this.category,
      brand: this.brand
    };
    this.showExportModal = true;
  }

  closeExportModal() {
    this.showExportModal = false;
  }

  submitExport() {
    const params: any = {
      sortBy: this.exportForm.sortBy,
      sortDir: this.exportForm.sortDir
    };
    if (this.exportForm.search) params.globalSearch = this.exportForm.search;
    if (this.exportForm.category) params.category = this.exportForm.category;
    if (this.exportForm.brand) params.brand = this.exportForm.brand;
    this.productService.exportProducts(params).subscribe((blob: Blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = 'products.xlsx';
      a.click();
      window.URL.revokeObjectURL(url);
      this.closeExportModal();
    });
  }
} 